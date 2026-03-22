package br.gov.saude.agendamento.repository;

import br.gov.saude.agendamento.domain.entity.CidadaoVinculacaoEquipe;
import br.gov.saude.agendamento.repository.projection.VinculoEquipeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface CidadaoVinculacaoEquipeRepository extends JpaRepository<CidadaoVinculacaoEquipe, Long> {

    @Query(value = """
            SELECT
                vcv.nu_cnes, vcv.nu_ine,
                us.co_seq_unidade_saude, us.no_unidade_saude,
                us.ds_logradouro, us.nu_numero, us.no_bairro, us.ds_cep,
                eq.co_seq_equipe, eq.no_equipe, eq.nu_ine AS ine_equipe
            FROM public.tb_cidadao_vinculacao_equipe vcv
            JOIN public.tb_unidade_saude us
              ON us.nu_cnes = vcv.nu_cnes AND us.st_ativo = 1
            JOIN public.tb_equipe eq
              ON eq.nu_ine = vcv.nu_ine
             AND eq.co_unidade_saude = us.co_seq_unidade_saude
             AND eq.st_ativo = 1
            WHERE vcv.co_cidadao = :coSeqCidadao
            ORDER BY vcv.dt_atualizacao_cadastro DESC
            LIMIT 1
            """, nativeQuery = true)
    Optional<VinculoEquipeProjection> buscarVinculoMaisRecente(Long coSeqCidadao);
}

