package br.gov.saude.agendamento.exception;

public class SlotIndisponivelException extends RuntimeException {
    public SlotIndisponivelException(String message) {
        super(message);
    }
}

