import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class EstoqueCafeAppTest {

    @Test
    void adicionarQuantidadeDeveAcrescentarAoEstoque() {
        EstoqueCafeApp.Produto produto = new EstoqueCafeApp.Produto(
                1,
                "Fertilizante",
                EstoqueCafeApp.Categoria.FERTILIZANTE_FOLIAR,
                "Nitrogênio",
                "AgroMais",
                "MAPA-123",
                "L",
                10.0,
                5.0,
                LocalDate.now().plusDays(60)
        );

        produto.adicionarQuantidade(7.5);

        assertEquals(17.5, produto.getQuantidade());
    }

    @Test
    void removerQuantidadeValidaDeveAtualizarEstoque() {
        EstoqueCafeApp.Produto produto = new EstoqueCafeApp.Produto(
                2,
                "Inseticida",
                EstoqueCafeApp.Categoria.INSETICIDA,
                "Clorpirifós",
                "Campo Verde",
                "MAPA-456",
                "L",
                20.0,
                8.0,
                LocalDate.now().plusDays(90)
        );

        boolean removido = produto.removerQuantidade(5.5);

        assertTrue(removido);
        assertEquals(14.5, produto.getQuantidade());
    }

    @Test
    void removerQuantidadeInvalidaDeveRetornarFalso() {
        EstoqueCafeApp.Produto produto = new EstoqueCafeApp.Produto(
                3,
                "Herbicida",
                EstoqueCafeApp.Categoria.HERBICIDA,
                "Glifosato",
                "Cultiva",
                "MAPA-789",
                "L",
                4.0,
                3.0,
                LocalDate.now().plusDays(30)
        );

        boolean removido = produto.removerQuantidade(6.0);

        assertFalse(removido);
        assertEquals(4.0, produto.getQuantidade());
    }

    @Test
    void estoqueBaixoDeveSerVerdadeiroQuandoQuantidadeAtingeMinimo() {
        EstoqueCafeApp.Produto produto = new EstoqueCafeApp.Produto(
                4,
                "Fungicida",
                EstoqueCafeApp.Categoria.FUNGICIDA,
                "Azoxistrobina",
                "Solução Rural",
                "MAPA-321",
                "Kg",
                5.0,
                5.0,
                LocalDate.now().plusDays(45)
        );

        assertTrue(produto.estoqueBaixo());
    }

    @Test
    void vencidoDeveSerVerdadeiroQuandoDataJaPassou() {
        EstoqueCafeApp.Produto produto = new EstoqueCafeApp.Produto(
                5,
                "Biológico",
                EstoqueCafeApp.Categoria.BIOLOGICO,
                "Trichoderma",
                "BioAgrícola",
                "MAPA-654",
                "Kg",
                10.0,
                2.0,
                LocalDate.now().minusDays(1)
        );

        assertTrue(produto.vencido());
    }

    @Test
    void venceEmBreveDeveSerVerdadeiroQuandoFaltaMenosDe90Dias() {
        EstoqueCafeApp.Produto produto = new EstoqueCafeApp.Produto(
                6,
                "Acaricida",
                EstoqueCafeApp.Categoria.ACARICIDA,
                "Abamectina",
                "Protege",
                "MAPA-987",
                "L",
                12.0,
                2.0,
                LocalDate.now().plusDays(45)
        );

        assertTrue(produto.venceEmBreve());
    }
}
