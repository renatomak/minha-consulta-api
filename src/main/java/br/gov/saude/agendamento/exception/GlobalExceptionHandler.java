package br.gov.saude.agendamento.exception;

import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    @ExceptionHandler(CidadaoNaoEncontradoException.class)
    public ResponseEntity<ProblemDetail> handleCidadaoNaoEncontrado(CidadaoNaoEncontradoException ex) {
        return problem(HttpStatus.NOT_FOUND, "Cidadao nao encontrado", ex.getMessage());
    }

    @ExceptionHandler(VinculoNaoEncontradoException.class)
    public ResponseEntity<ProblemDetail> handleVinculoNaoEncontrado(VinculoNaoEncontradoException ex) {
        return problem(HttpStatus.NOT_FOUND, "Cidadao sem equipe vinculada", ex.getMessage());
    }

    @ExceptionHandler(LotacaoNaoEncontradaException.class)
    public ResponseEntity<ProblemDetail> handleLotacaoNaoEncontrada(LotacaoNaoEncontradaException ex) {
        return problem(HttpStatus.NOT_FOUND, "Lotacao nao encontrada", ex.getMessage());
    }

    @ExceptionHandler(AgendaNaoConfiguradaException.class)
    public ResponseEntity<ProblemDetail> handleAgendaNaoConfigurada(AgendaNaoConfiguradaException ex) {
        return problem(HttpStatus.UNPROCESSABLE_ENTITY, "Agenda nao configurada", ex.getMessage());
    }

    @ExceptionHandler(SlotIndisponivelException.class)
    public ResponseEntity<ProblemDetail> handleSlotIndisponivel(SlotIndisponivelException ex) {
        return problem(HttpStatus.CONFLICT, "Slot indisponivel", ex.getMessage());
    }

    @ExceptionHandler(AgendamentoDuplicadoException.class)
    public ResponseEntity<ProblemDetail> handleAgendamentoDuplicado(AgendamentoDuplicadoException ex) {
        return problem(HttpStatus.CONFLICT, "Agendamento duplicado", ex.getMessage());
    }

    @ExceptionHandler(CancelamentoInvalidoException.class)
    public ResponseEntity<ProblemDetail> handleCancelamentoInvalido(CancelamentoInvalidoException ex) {
        return problem(HttpStatus.CONFLICT, "Cancelamento invalido", ex.getMessage());
    }

    @ExceptionHandler(PeriodoMaximoExcedidoException.class)
    public ResponseEntity<ProblemDetail> handlePeriodoMaximoExcedido(PeriodoMaximoExcedidoException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Periodo excede o limite", ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ProblemDetail> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Dados invalidos", "Parametro invalido: " + ex.getName());
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ProblemDetail> handleRecursoNaoEncontrado(RecursoNaoEncontradoException ex) {
        return problem(HttpStatus.NOT_FOUND, "Recurso nao encontrado", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ProblemDetail> handleIllegalArgument(IllegalArgumentException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Dados invalidos", ex.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ProblemDetail> handleConstraintViolation(ConstraintViolationException ex) {
        return problem(HttpStatus.BAD_REQUEST, "Dados invalidos", ex.getMessage());
    }

    @ExceptionHandler(Throwable.class)
    public ResponseEntity<ProblemDetail> handleThrowable(Throwable ex) {
        return problem(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno", ex.getMessage());
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
            MethodArgumentNotValidException ex,
            HttpHeaders headers,
            HttpStatusCode status,
            WebRequest request
    ) {
        String detail = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(this::formatFieldError)
                .collect(Collectors.joining("; "));
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, detail);
        pd.setTitle("Dados invalidos");
        return ResponseEntity.badRequest().body(pd);
    }

    private String formatFieldError(FieldError error) {
        return error.getField() + ": " + error.getDefaultMessage();
    }

    private ResponseEntity<ProblemDetail> problem(HttpStatus status, String title, String detail) {
        ProblemDetail pd = ProblemDetail.forStatusAndDetail(status, detail == null ? "Erro na requisicao." : detail);
        pd.setTitle(title);
        return ResponseEntity.of(pd).build();
    }
}
