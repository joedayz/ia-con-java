package dev.springai.workshop.car.workflow;

import dev.springai.workshop.car.agent.CarConditionFeedbackAgent;
import dev.springai.workshop.car.domain.CarConditions;
import dev.springai.workshop.car.domain.CarInfo;
import dev.springai.workshop.agentic.ChainWorkflow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Workflow secuencial con {@link ChainWorkflow#chainSteps}: feedback → asignación → condición.
 */
@Service
public class CarProcessingWorkflow {

    private static final Logger log = LoggerFactory.getLogger(CarProcessingWorkflow.class);

    private final ChainWorkflow chainWorkflow;
    private final FeedbackWorkflow feedbackWorkflow;
    private final CarAssignmentWorkflow carAssignmentWorkflow;
    private final CarConditionFeedbackAgent carConditionFeedbackAgent;

    public CarProcessingWorkflow(
            ChainWorkflow chainWorkflow,
            FeedbackWorkflow feedbackWorkflow,
            CarAssignmentWorkflow carAssignmentWorkflow,
            CarConditionFeedbackAgent carConditionFeedbackAgent) {
        this.chainWorkflow = chainWorkflow;
        this.feedbackWorkflow = feedbackWorkflow;
        this.carAssignmentWorkflow = carAssignmentWorkflow;
        this.carConditionFeedbackAgent = carConditionFeedbackAgent;
    }

    public CarConditions processCarReturn(CarInfo carInfo, Integer carNumber, String feedback) {
        CarReturnContext result = chainWorkflow.chainSteps(
                CarReturnContext.initial(carInfo, carNumber, feedback),
                List.of(
                        this::runParallelFeedback,
                        this::runAssignment,
                        this::runConditionAnalysis));

        return result.carConditions();
    }

    private CarReturnContext runParallelFeedback(CarReturnContext ctx) {
        var feedbackResult = feedbackWorkflow.analyzeFeedback(ctx.carInfo(), ctx.carNumber(), ctx.feedback());
        return ctx.withFeedbackResult(feedbackResult);
    }

    private CarReturnContext runAssignment(CarReturnContext ctx) {
        var fr = ctx.feedbackResult();
        String outcome = carAssignmentWorkflow.processAction(
                ctx.carInfo(), ctx.carNumber(),
                fr.cleaningRequest(), fr.maintenanceRequest());
        return ctx.withAssignmentOutcome(outcome);
    }

    private CarReturnContext runConditionAnalysis(CarReturnContext ctx) {
        var fr = ctx.feedbackResult();
        log.info("CarConditionFeedbackAgent updating...");
        String carCondition = carConditionFeedbackAgent.analyzeForCondition(
                ctx.carInfo(), ctx.carNumber(),
                fr.cleaningRequest(), fr.maintenanceRequest());

        CarConditions conditions = new CarConditions(carCondition, ctx.resolveAssignment());
        return ctx.withCarConditions(conditions);
    }
}
