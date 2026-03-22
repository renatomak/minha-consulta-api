package br.gov.saude.agendamento.repository;

import br.gov.saude.agendamento.domain.entity.Lotacao;
import br.gov.saude.agendamento.repository.projection.LotacaoDetalheProjection;
import br.gov.saude.agendamento.repository.projection.ProfissionalEquipeProjection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface LotacaoRepository extends JpaRepository<Lotacao, Long> {

    @Query("""
            select l from Lotacao l
            where l.coAtorPapel = :coLotacao
              and l.dataDesativacaoLotacao is null
            """)
    Optional<Lotacao> buscarLotacaoAtiva(Long coLotacao);

    @Query(value = """
            SELECT
                lot.co_ator_papel AS co_lotacao,
                p.co_seq_prof,
                COALESCE(p.no_social_profissional, p.no_civil_profissional) AS nome,
                cbo.no_cbo,
                cbo.co_cbo,
                CASE WHEN EXISTS (
                    SELECT 1 FROM public.tb_cfg_agenda ca
                    WHERE ca.co_entidade_configurada = lot.co_ator_papel
                ) THEN true ELSE false END AS has_agenda
            FROM public.tb_lotacao lot
            JOIN public.tb_prof p  ON p.co_seq_prof = lot.co_prof
            JOIN public.tb_cbo cbo ON cbo.co_cbo    = lot.co_cbo
            WHERE lot.co_equipe = :coSeqEquipe
              AND lot.dt_desativacao_lotacao IS NULL
            ORDER BY cbo.no_cbo, p.no_civil_profissional
            """, nativeQuery = true)
    List<ProfissionalEquipeProjection> listarProfissionaisPorEquipe(Long coSeqEquipe);

    @Query(value = """
            SELECT
                lot.co_ator_papel AS co_lotacao,
                COALESCE(p.no_social_profissional, p.no_civil_profissional) AS nome_profissional,
                cbo.no_cbo
            FROM public.tb_lotacao lot
            JOIN public.tb_prof p ON p.co_seq_prof = lot.co_prof
            JOIN public.tb_cbo cbo ON cbo.co_cbo = lot.co_cbo
            WHERE lot.co_ator_papel = :coLotacao
            """, nativeQuery = true)
    Optional<LotacaoDetalheProjection> buscarDetalheLotacao(Long coLotacao);
}

