package br.gov.saude.agendamento.exception;

public class AgendamentoDuplicadoException extends RuntimeException {
    public AgendamentoDuplicadoException(String message) {
        super(message);
    }
}

