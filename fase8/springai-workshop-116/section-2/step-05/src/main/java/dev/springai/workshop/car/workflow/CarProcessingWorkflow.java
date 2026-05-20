package dev.springai.workshop.car.workflow;

import dev.springai.workshop.car.agent.CarConditionFeedbackAgent;
import dev.springai.workshop.car.agent.FleetSupervisorAgent;
import dev.springai.workshop.car.domain.CarConditions;
import dev.springai.workshop.car.domain.CarInfo;
import dev.springai.workshop.car.domain.FeedbackTask;
import dev.springai.workshop.agentic.ChainWorkflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarProcessingWorkflow {

    private static final Logger log = LoggerFactory.getLogger(CarProcessingWorkflow.class);

    private final ChainWorkflow chainWorkflow;
    private final FeedbackAnalysisWorkflow feedbackAnalysisWorkflow;
    private final FleetSupervisorAgent fleetSupervisorAgent;
    private final CarConditionFeedbackAgent carConditionFeedbackAgent;

    public CarProcessingWorkflow(
            ChainWorkflow chainWorkflow,
            FeedbackAnalysisWorkflow feedbackAnalysisWorkflow,
            FleetSupervisorAgent fleetSupervisorAgent,
            CarConditionFeedbackAgent carConditionFeedbackAgent) {
        this.chainWorkflow = chainWorkflow;
        this.feedbackAnalysisWorkflow = feedbackAnalysisWorkflow;
        this.fleetSupervisorAgent = fleetSupervisorAgent;
        this.carConditionFeedbackAgent = carConditionFeedbackAgent;
    }

    public CarConditions processCarReturn(
            List<FeedbackTask> tasks,
            CarInfo carInfo,
            Integer carNumber,
            String feedback) {

        FleetProcessingContext result = chainWorkflow.chainSteps(
                FleetProcessingContext.initial(tasks, carInfo, carNumber, feedback),
                List.of(
                        this::runParallelAnalysis,
                        this::runSupervisor,
                        this::runConditionUpdate));

        return result.carConditions();
    }

    private FleetProcessingContext runParallelAnalysis(FleetProcessingContext ctx) {
        var analysis = feedbackAnalysisWorkflow.analyzeFeedback(
                ctx.tasks(), ctx.carInfo(), ctx.carNumber(), ctx.feedback());
        return ctx.withAnalysis(analysis);
    }

    private FleetProcessingContext runSupervisor(FleetProcessingContext ctx) {
        String decision = fleetSupervisorAgent.superviseCarProcessing(
                ctx.carInfo(), ctx.carNumber(), ctx.feedback(), ctx.analysisResults());
        return ctx.withSupervisorDecision(decision);
    }

    private FleetProcessingContext runConditionUpdate(FleetProcessingContext ctx) {
        log.info("CarConditionFeedbackAgent updating...");
        CarConditions conditions = carConditionFeedbackAgent.analyzeForCondition(
                ctx.carInfo(), ctx.carNumber(), ctx.analysisResults(), ctx.supervisorDecision());
        log.debug("CarConditions: {} → {} ({})",
                conditions.generalCondition(),
                conditions.carAssignment(),
                conditions.dispositionStatus());
        return ctx.withCarConditions(conditions);
    }
}
