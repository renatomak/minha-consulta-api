package br.gov.saude.agendamento.repository;

import br.gov.saude.agendamento.domain.entity.Cidadao;
import br.gov.saude.agendamento.repository.projection.CidadaoCpfProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CidadaoRepository extends JpaRepository<Cidadao, Long> {

    @Query(value = """
            SELECT
                c.co_seq_cidadao,
                COALESCE(c.no_social, c.no_cidadao) AS nome,
                c.nu_cpf, c.nu_cns, c.dt_nascimento, c.no_sexo,
                c.nu_telefone_celular, c.ds_email,
                p.co_seq_prontuario AS co_prontuario
            FROM public.tb_cidadao c
            LEFT JOIN public.tb_prontuario p ON p.co_cidadao = c.co_seq_cidadao
            WHERE c.nu_cpf = :cpf AND c.st_ativo = 1
            LIMIT 1
            """, nativeQuery = true)
    Optional<CidadaoCpfProjection> buscarPorCpf(String cpf);
}

