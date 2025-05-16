package com.springfield.springfield_marty;

import org.springframework.context.annotation.Configuration;

@Configuration
@EnableStateMachineFactory
public class StateMachineConfig extends StateMachineConfigurerAdapter<RequestState, RequestEvent> {
    @Override
    public void configure(StateMachineStateConfigurer<RequestState, RequestEvent> states) throws Exception {
        states
            .withStates()
            .initial(RequestState.SOLICITADO)
            .states(EnumSet.allOf(RequestState.class));
    }

    @Override
    public void configure(StateMachineTransitionConfigurer<RequestState, RequestEvent> transitions) throws Exception {
        transitions
            .withExternal().source(RequestState.SOLICITADO).target(RequestState.AGUARDANDO_ANALISE).event(RequestEvent.ANALISAR)
            .and()
            .withExternal().source(RequestState.AGUARDANDO_ANALISE).target(RequestState.CONCLUIDO).event(RequestEvent.CONCLUIR);
    }

    // Listener para gravação de histórico
    @Bean
    public StateMachineListener<RequestState, RequestEvent> listener(RequestHistoryRepository repo) {
        return new StateMachineListenerAdapter<RequestState, RequestEvent>() {
            @Override
            public void stateChanged(State<RequestState, RequestEvent> from, State<RequestState, RequestEvent> to) {
                RequestHistory hist = new RequestHistory();
                hist.setCitizenId((Integer) from.getExtendedState().getVariables().get("citizenId"));
                hist.setTimestamp(LocalDateTime.now());
                hist.setState(to.getId());
                hist.setDescription((String) from.getExtendedState().getVariables().get("description"));
                repo.save(hist);
            }
        };
    }
}
