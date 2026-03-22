package br.gov.saude.agendamento.exception;

public class CancelamentoInvalidoException extends RuntimeException {
    public CancelamentoInvalidoException(String message) {
        super(message);
    }
}

