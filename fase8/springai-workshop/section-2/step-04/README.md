Nuevo requisito de Miles of Smiles Management: Decisiones de disposición inteligentes


El equipo directivo de Miles of Smiles ha identificado un nuevo reto: deben tomar decisiones inteligentes sobre
qué hacer con los vehículos cuando estos regresan con daños graves.

El sistema necesita:

- Detectar daños graves que podrían hacer que reparar un coche no sea rentable.
- Estimar el valor del vehículo para fundamentar las decisiones de disposición.
- Decida la estrategia de disposición (DESHACER, VENDER, DONAR o CONSERVAR) en función de:
  - Valor del automóvil
  - Edad del vehículo
  - Gravedad del daño
  - Estimaciones de costos de reparación

- Deje que un supervisor de IA orqueste todo el proceso de toma de decisiones.


Lo que aprenderás

- Comprender el patrón Supervisor y cuándo usarlo.
- Implementar un agente supervisor utilizando la @SupervisorAgentanotación
- Refactorizar tres analizadores de retroalimentación similares en un único analizador parametrizado.FeedbackAnalysisAgent
- La retroalimentación del modelo funciona como FeedbackTaskinstancias reutilizables.
- Se utiliza @ParallelMapperAgentpara invocar al mismo agente varias veces en paralelo.
- Utilice un @Outputmétodo para transformar los resultados brutos del flujo de trabajo en datos estructurados.
- Construir un modelo PricingAgentpara estimar los valores de mercado de los vehículos.
- Crea una DispositionAgent para tomar decisiones DESECHAR/VENDER/DONAR/CONSERVAR
- Vea cómo los supervisores proporcionan una orquestación autónoma y adaptativa.

¿Qué es un agente supervisor?

- Coordina de forma autónoma a otros (sub)agentes.
- Toma decisiones en tiempo de ejecución sobre qué agentes invocar.
- Se adapta al contexto utilizando reglas de negocio y condiciones actuales.
- Proporciona orquestación autónoma sin lógica de enrutamiento codificada.

Cuándo utilizar supervisores

- Enrutamiento sensible al contexto, donde las decisiones se basan en múltiples factores difíciles de predecir.
- Flexibilidad en las reglas de negocio que se ajusta más fácilmente mediante instrucciones que mediante cambios en el código.
- Orquestación compleja con múltiples agentes que tienen interdependencias.










