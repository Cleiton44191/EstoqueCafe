import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Control;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.control.TextInputDialog;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class EstoqueCafeApp extends Application {

    private static final String ARQUIVO = "estoque_cafe.dat";
    private static final DateTimeFormatter FORMATO_DATA =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private static final String ARQUIVO_FORMULAS = "formulas_cafe.dat";

    private final ObservableList<Produto> estoque =
            FXCollections.observableArrayList();
    private final ObservableList<Formula> formulas =
            FXCollections.observableArrayList();

    private TableView<Produto> tabela;
    private TextField campoPesquisa;

    enum Categoria {
        HERBICIDA,
        FUNGICIDA,
        INSETICIDA,
        ACARICIDA,
        NEMATICIDA,
        BIOLOGICO,
        ADJUVANTE,
        FERTILIZANTE_FOLIAR,
        OUTROS
    }

    public static class Produto implements Serializable {

        private static final long serialVersionUID = 1L;

        private int id;
        private String nome;
        private Categoria categoria;
        private String ingredienteAtivo;
        private String fabricante;
        private String registroMapa;
        private String unidade;
        private double quantidade;
        private double estoqueMinimo;
        private LocalDate validade;

        public Produto(
                int id,
                String nome,
                Categoria categoria,
                String ingredienteAtivo,
                String fabricante,
                String registroMapa,
                String unidade,
                double quantidade,
                double estoqueMinimo,
                LocalDate validade
        ) {
            this.id = id;
            this.nome = nome;
            this.categoria = categoria;
            this.ingredienteAtivo = ingredienteAtivo;
            this.fabricante = fabricante;
            this.registroMapa = registroMapa;
            this.unidade = unidade;
            this.quantidade = quantidade;
            this.estoqueMinimo = estoqueMinimo;
            this.validade = validade;
        }

        public int getId() {
            return id;
        }

        public String getNome() {
            return nome;
        }

        public Categoria getCategoria() {
            return categoria;
        }

        public String getIngredienteAtivo() {
            return ingredienteAtivo;
        }

        public String getFabricante() {
            return fabricante;
        }

        public String getRegistroMapa() {
            return registroMapa;
        }

        public String getUnidade() {
            return unidade;
        }

        public double getQuantidade() {
            return quantidade;
        }

        public double getEstoqueMinimo() {
            return estoqueMinimo;
        }

        public LocalDate getValidade() {
            return validade;
        }

        public void setNome(String nome) {
            this.nome = nome;
        }

        public void setCategoria(Categoria categoria) {
            this.categoria = categoria;
        }

        public void setIngredienteAtivo(String ingredienteAtivo) {
            this.ingredienteAtivo = ingredienteAtivo;
        }

        public void setFabricante(String fabricante) {
            this.fabricante = fabricante;
        }

        public void setRegistroMapa(String registroMapa) {
            this.registroMapa = registroMapa;
        }

        public void setUnidade(String unidade) {
            this.unidade = unidade;
        }

        public void setQuantidade(double quantidade) {
            this.quantidade = quantidade;
        }

        public void setEstoqueMinimo(double estoqueMinimo) {
            this.estoqueMinimo = estoqueMinimo;
        }

        public void setValidade(LocalDate validade) {
            this.validade = validade;
        }

        public void adicionarQuantidade(double valor) {
            quantidade += valor;
        }

        public boolean removerQuantidade(double valor) {
            if (valor <= 0 || valor > quantidade) {
                return false;
            }

            quantidade -= valor;
            return true;
        }

        public boolean estoqueBaixo() {
            return quantidade <= estoqueMinimo;
        }

        public boolean vencido() {
            return validade.isBefore(LocalDate.now());
        }

        public boolean venceEmBreve() {
            LocalDate limite = LocalDate.now().plusDays(90);
            return !vencido() && !validade.isAfter(limite);
        }
    }

    public static class IngredienteFormula implements Serializable {
        private static final long serialVersionUID = 1L;

        private int produtoId;
        private String produtoNome;
        private double quantidade;

        public IngredienteFormula(int produtoId, String produtoNome, double quantidade) {
            this.produtoId = produtoId;
            this.produtoNome = produtoNome;
            this.quantidade = quantidade;
        }

        public int getProdutoId() { return produtoId; }
        public String getProdutoNome() { return produtoNome; }
        public double getQuantidade() { return quantidade; }

        public void setProdutoId(int produtoId) { this.produtoId = produtoId; }
        public void setProdutoNome(String produtoNome) { this.produtoNome = produtoNome; }
        public void setQuantidade(double quantidade) { this.quantidade = quantidade; }
    }

    public static class Formula implements Serializable {
        private static final long serialVersionUID = 1L;

        private int id;
        private String nome;
        private List<IngredienteFormula> ingredientes;

        public Formula(int id, String nome) {
            this.id = id;
            this.nome = nome;
            this.ingredientes = new ArrayList<>();
        }

        public int getId() { return id; }
        public String getNome() { return nome; }
        public List<IngredienteFormula> getIngredientes() { return ingredientes; }

        public void setNome(String nome) { this.nome = nome; }

        public void adicionarIngrediente(IngredienteFormula ingrediente) {
            ingredientes.add(ingrediente);
        }

        public void removerIngrediente(IngredienteFormula ingrediente) {
            ingredientes.remove(ingrediente);
        }
    }

    @Override
    public void start(Stage stage) {
        carregarEstoque();
        carregarFormulas();

        BorderPane raiz = new BorderPane();
        raiz.getStyleClass().add("root");

        raiz.setTop(criarCabecalho());
        raiz.setCenter(criarConteudo());

        Scene cena = new Scene(raiz, 1200, 720);
        cena.getStylesheets().add(
                Objects.requireNonNull(
                        getClass().getResource("/estilo.css")
                ).toExternalForm()
        );

        stage.setTitle("Estoque de Produtos - Café");
        stage.setMinWidth(700);
        stage.setMinHeight(500);
        stage.setScene(cena);
        stage.show();
    }

    private VBox criarCabecalho() {
        Label titulo = new Label("Estoque de Café");
        titulo.getStyleClass().add("titulo");

        VBox container = new VBox(titulo);
        container.getStyleClass().add("cabecalho");
        container.setPadding(new Insets(15));
        container.setAlignment(Pos.CENTER_LEFT);

        return container;
    }

    private VBox criarConteudo() {
        HBox controles = criarControles();
        tabela = criarTabela();

        VBox conteudo = new VBox(controles, tabela);
        VBox.setVgrow(tabela, Priority.ALWAYS);

        return conteudo;
    }

    private HBox criarControles() {
        campoPesquisa = new TextField();
        campoPesquisa.setPromptText("Pesquisar produto...");
        campoPesquisa.textProperty().addListener((obs, antigo, novo) ->
                atualizarTabela()
        );

        Button cadastrar = new Button("Novo produto");
        cadastrar.getStyleClass().add("botao-principal");
        cadastrar.setOnAction(e -> abrirFormulario(null));

        Button entrada = new Button("Entrada");
        entrada.getStyleClass().add("botao-verde");
        entrada.setOnAction(e -> registrarEntrada());

        Button saida = new Button("Saída");
        saida.getStyleClass().add("botao-vermelho");
        saida.setOnAction(e -> registrarSaida());

        Button formulas = new Button("Fórmulas");
        formulas.getStyleClass().add("botao-secundario");
        formulas.setOnAction(e -> gerenciarFormulas());

        Button salvar = new Button("Salvar");
        salvar.getStyleClass().add("botao-secundario");
        salvar.setOnAction(e -> {
            salvarEstoque();
            salvarFormulas();
            mostrarMensagem("Dados salvos com sucesso.");
        });

        HBox controles = new HBox(
                10,
                campoPesquisa,
                cadastrar,
                entrada,
                saida,
                formulas,
                salvar
        );

        controles.setPadding(new Insets(10, 20, 15, 20));
        controles.setAlignment(Pos.CENTER_LEFT);
        HBox.setHgrow(campoPesquisa, Priority.ALWAYS);

        return controles;
    }

    private TableView<Produto> criarTabela() {
        TableView<Produto> novaTabela = new TableView<>();
        novaTabela.setItems(estoque);
        novaTabela.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Produto, Integer> id = new TableColumn<>("ID");
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
        id.setMaxWidth(70);

        TableColumn<Produto, String> nome = new TableColumn<>("Produto");
        nome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Produto, Categoria> categoria =
                new TableColumn<>("Categoria");
        categoria.setCellValueFactory(
                new PropertyValueFactory<>("categoria")
        );

        TableColumn<Produto, String> quantidade =
                new TableColumn<>("Quantidade (L)");
        quantidade.setCellValueFactory(celula -> {
            Produto produto = celula.getValue();
            return javafx.beans.binding.Bindings.createStringBinding(
                () -> String.format(Locale.US, "%.2f L", produto.getQuantidade())
            );
        });
        quantidade.setPrefWidth(120);

        TableColumn<Produto, Void> acoes =
                new TableColumn<>("Ações");

        acoes.setCellFactory(coluna -> new TableCell<>() {
            private final Button editar = new Button("Editar");
            private final Button excluir = new Button("Excluir");
            private final HBox painel = new HBox(5, editar, excluir);

            {
                editar.getStyleClass().add("botao-editar");
                excluir.getStyleClass().add("botao-excluir");

                editar.setOnAction(e -> {
                    Produto produto = getTableView()
                            .getItems()
                            .get(getIndex());

                    abrirFormulario(produto);
                });

                excluir.setOnAction(e -> {
                    Produto produto = getTableView()
                            .getItems()
                            .get(getIndex());

                    removerProduto(produto);
                });

                painel.setAlignment(Pos.CENTER);
            }

            @Override
            protected void updateItem(Void item, boolean vazio) {
                super.updateItem(item, vazio);
                setGraphic(vazio ? null : painel);
            }
        });

        novaTabela.getColumns().addAll(
                id,
                nome,
                categoria,
                quantidade,
                acoes
        );

        return novaTabela;
    }

    private void atualizarTabela() {
        String termo = campoPesquisa.getText() == null
                ? ""
                : campoPesquisa.getText().toLowerCase();

        tabela.setItems(
                estoque.filtered(produto -> {
                    return termo.isBlank()
                                    || produto.getNome()
                                    .toLowerCase()
                                    .contains(termo);
                })
        );
    }

    private void abrirFormulario(Produto produtoExistente) {
        boolean editando = produtoExistente != null;

        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle(editando
                ? "Editar produto"
                : "Novo produto");

        ButtonType confirmar = new ButtonType(
                editando ? "Salvar alterações" : "Cadastrar",
                ButtonBar.ButtonData.OK_DONE
        );

        dialogo.getDialogPane().getButtonTypes().addAll(
                confirmar,
                ButtonType.CANCEL
        );

        TextField nome = new TextField();
        TextField quantidade = new TextField();

        ComboBox<Categoria> categoria = new ComboBox<>();
        categoria.getItems().addAll(Categoria.values());

        if (editando) {
            nome.setText(produtoExistente.getNome());
            categoria.setValue(produtoExistente.getCategoria());
            quantidade.setText(String.valueOf(
                    produtoExistente.getQuantidade()
            ));
        }

        GridPane formulario = new GridPane();
        formulario.setHgap(10);
        formulario.setVgap(10);
        formulario.setPadding(new Insets(20));

        adicionarCampo(formulario, "Nome comercial:", nome, 0);
        adicionarCampo(formulario, "Categoria:", categoria, 1);
        adicionarCampo(formulario, "Quantidade:", quantidade, 2);

        dialogo.getDialogPane().setContent(formulario);

        Node botaoConfirmar =
                dialogo.getDialogPane().lookupButton(confirmar);

        botaoConfirmar.addEventFilter(
                javafx.event.ActionEvent.ACTION,
                evento -> {
                    try {
                        validarFormulario(nome, categoria, quantidade);
                    } catch (IllegalArgumentException erro) {
                        mostrarErro(erro.getMessage());
                        evento.consume();
                    }
                }
        );

        dialogo.setResultConverter(botao ->
                botao == confirmar ? confirmar : null
        );

        Optional<ButtonType> resultado = dialogo.showAndWait();

        if (resultado.isPresent() && resultado.get() == confirmar) {
            try {
                String nomeValor = nome.getText().trim();
                Categoria categoriaValor = categoria.getValue();
                double quantidadeValor = converterDouble(
                        quantidade.getText()
                );

                if (editando) {
                    produtoExistente.setNome(nomeValor);
                    produtoExistente.setCategoria(categoriaValor);
                    produtoExistente.setQuantidade(quantidadeValor);
                } else {
                    estoque.add(new Produto(
                            proximoId(),
                            nomeValor,
                            categoriaValor,
                            "",
                            "",
                            "",
                            "unidade",
                            quantidadeValor,
                            0,
                            LocalDate.now().plusYears(1)
                    ));
                }

                salvarEstoque();
                atualizarTabela();

            } catch (Exception erro) {
                mostrarErro("Não foi possível salvar o produto.");
            }
        }
    }

    private void adicionarCampo(
            GridPane painel,
            String texto,
            Control campo,
            int linha
    ) {
        Label label = new Label(texto);
        label.getStyleClass().add("label-formulario");

        painel.add(label, 0, linha);
        painel.add(campo, 1, linha);

        GridPane.setHgrow(campo, Priority.ALWAYS);

        if (campo instanceof TextInputControl entrada) {
            entrada.setPrefWidth(350);
        } else {
            campo.setPrefWidth(350);
        }
    }

    private void validarFormulario(
            TextField nome,
            ComboBox<Categoria> categoria,
            TextField quantidade
    ) {
        if (nome.getText().isBlank()) {
            throw new IllegalArgumentException(
                    "Preencha o nome do produto."
            );
        }

        if (categoria.getValue() == null) {
            throw new IllegalArgumentException(
                    "Selecione uma categoria."
            );
        }

        double quantidadeValor = converterDouble(
                quantidade.getText()
        );

        if (quantidadeValor < 0) {
            throw new IllegalArgumentException(
                    "A quantidade não pode ser negativa."
            );
        }
    }

    private double converterDouble(String texto) {
        try {
            return Double.parseDouble(
                    texto.trim().replace(",", ".")
            );
        } catch (NumberFormatException erro) {
            throw new IllegalArgumentException(
                    "Digite valores numéricos válidos."
            );
        }
    }

    private void registrarEntrada() {
        Produto produto = produtoSelecionado();

        if (produto == null) {
            return;
        }

        TextInputDialog dialogo = new TextInputDialog();
        dialogo.setTitle("Entrada de estoque");
        dialogo.setHeaderText("Produto: " + produto.getNome());
        dialogo.setContentText("Quantidade recebida (L):");
        dialogo.getEditor().setPrefWidth(200);

        Optional<String> resultado = dialogo.showAndWait();

        resultado.ifPresent(valor -> {
            if (valor.trim().isEmpty()) {
                mostrarErro("Informe uma quantidade");
                return;
            }

            try {
                double quantidade = converterDouble(valor);

                if (quantidade <= 0) {
                    throw new IllegalArgumentException(
                            "A quantidade deve ser maior que zero."
                    );
                }

                produto.adicionarQuantidade(quantidade);
                salvarEstoque();
                
                Platform.runLater(() -> {
                    tabela.refresh();
                    atualizarTabela();
                    mostrarMensagem("Entrada registrada: +" + String.format("%.2f", quantidade) + " L");
                });

            } catch (IllegalArgumentException erro) {
                mostrarErro(erro.getMessage());
            }
        });
    }

    private void registrarSaida() {
        Produto produto = produtoSelecionado();

        if (produto == null) {
            return;
        }

        TextInputDialog dialogo = new TextInputDialog();
        dialogo.setTitle("Saída de estoque");
        dialogo.setHeaderText("Produto: " + produto.getNome());
        dialogo.setContentText("Quantidade utilizada (L):");
        dialogo.getEditor().setPrefWidth(200);

        Optional<String> resultado = dialogo.showAndWait();

        resultado.ifPresent(valor -> {
            if (valor.trim().isEmpty()) {
                mostrarErro("Informe uma quantidade");
                return;
            }

            try {
                double quantidade = converterDouble(valor);

                if (quantidade <= 0) {
                    throw new IllegalArgumentException(
                            "A quantidade deve ser maior que zero."
                    );
                }

                if (!produto.removerQuantidade(quantidade)) {
                    throw new IllegalArgumentException(
                            "Estoque insuficiente. Disponível: " + 
                            String.format("%.2f", produto.getQuantidade() + quantidade) + " L"
                    );
                }

                salvarEstoque();
                
                Platform.runLater(() -> {
                    tabela.refresh();
                    atualizarTabela();
                    mostrarMensagem("Saída registrada: -" + String.format("%.2f", quantidade) + " L");
                });

            } catch (IllegalArgumentException erro) {
                mostrarErro(erro.getMessage());
            }
        });
    }

    private Produto produtoSelecionado() {
        Produto produto = tabela.getSelectionModel()
                .getSelectedItem();

        if (produto == null) {
            mostrarErro(
                    "Selecione um produto na tabela primeiro."
            );
        }

        return produto;
    }

    private void removerProduto(Produto produto) {
        Alert alerta = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Deseja remover o produto \""
                        + produto.getNome()
                        + "\"?",
                ButtonType.YES,
                ButtonType.NO
        );

        alerta.setTitle("Confirmar remoção");
        alerta.setHeaderText("Remover produto");

        Optional<ButtonType> resposta = alerta.showAndWait();

        if (resposta.isPresent()
                && resposta.get() == ButtonType.YES) {
            estoque.remove(produto);
            salvarEstoque();
            atualizarTabela();
        }
    }

    private int proximoId() {
        return estoque.stream()
                .mapToInt(Produto::getId)
                .max()
                .orElse(0) + 1;
    }

    private void salvarEstoque() {
        try (ObjectOutputStream saida =
                     new ObjectOutputStream(
                             new FileOutputStream(ARQUIVO))) {
            saida.writeObject(new ArrayList<>(estoque));
        } catch (IOException erro) {
            mostrarErro("Erro ao salvar os dados.");
        }
    }

    @SuppressWarnings("unchecked")
    private void carregarEstoque() {
        File arquivo = new File(ARQUIVO);

        if (!arquivo.exists()) {
            return;
        }

        try (ObjectInputStream entrada =
                     new ObjectInputStream(
                             new FileInputStream(arquivo))) {

            List<Produto> dados =
                    (List<Produto>) entrada.readObject();

            estoque.setAll(dados);

        } catch (IOException | ClassNotFoundException erro) {
            mostrarErro("Não foi possível carregar os dados salvos.");
        }
    }

    private void mostrarMensagem(String mensagem) {
        Alert alerta = new Alert(
                Alert.AlertType.INFORMATION,
                mensagem,
                ButtonType.OK
        );

        alerta.setTitle("Estoque Café");
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    private void mostrarErro(String mensagem) {
        Alert alerta = new Alert(
                Alert.AlertType.ERROR,
                mensagem,
                ButtonType.OK
        );

        alerta.setTitle("Atenção");
        alerta.setHeaderText(null);
        alerta.showAndWait();
    }

    private int proximoIdFormula() {
        return formulas.stream()
                .mapToInt(Formula::getId)
                .max()
                .orElse(0) + 1;
    }

    private void gerenciarFormulas() {
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle("Gerenciar Fórmulas de Tanques");
        dialogo.setHeaderText("Crie e gerencie suas fórmulas de preparação");
        dialogo.setResizable(true);

        VBox conteudo = new VBox(10);
        conteudo.setPadding(new Insets(20));
        conteudo.setPrefSize(600, 400);

        ObservableList<Formula> listaFormulas = FXCollections.observableArrayList(formulas);
        TableView<Formula> tabelaFormulas = new TableView<>(listaFormulas);
        tabelaFormulas.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Formula, Integer> colId = new TableColumn<>("ID");
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colId.setMaxWidth(50);

        TableColumn<Formula, String> colNome = new TableColumn<>("Nome");
        colNome.setCellValueFactory(new PropertyValueFactory<>("nome"));

        TableColumn<Formula, Void> colAcoes = new TableColumn<>("Ações");
        colAcoes.setMinWidth(250);
        colAcoes.setCellFactory(param -> new TableCell<Formula, Void>() {
            private final Button editar = new Button("Editar");
            private final Button aplicar = new Button("Aplicar");
            private final Button remover = new Button("Remover");

            {
                editar.setStyle("-fx-padding: 5px; -fx-font-size: 11px;");
                aplicar.setStyle("-fx-padding: 5px; -fx-font-size: 11px;");
                remover.setStyle("-fx-padding: 5px; -fx-font-size: 11px;");

                editar.setOnAction(e -> {
                    Formula formula = this.getTableView().getItems().get(this.getIndex());
                    abrirDialogoFormula(formula);
                });
                aplicar.setOnAction(e -> {
                    Formula formula = this.getTableView().getItems().get(this.getIndex());
                    aplicarFormula(formula);
                });
                remover.setOnAction(e -> {
                    int index = this.getIndex();
                    if (index >= 0 && index < this.getTableView().getItems().size()) {
                        this.getTableView().getItems().remove(index);
                        formulas.remove(index);
                        salvarFormulas();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || this.getIndex() < 0) {
                    setGraphic(null);
                } else {
                    HBox box = new HBox(5, editar, aplicar, remover);
                    box.setAlignment(Pos.CENTER);
                    setGraphic(box);
                }
            }
        });

        tabelaFormulas.getColumns().addAll(colId, colNome, colAcoes);
        VBox.setVgrow(tabelaFormulas, Priority.ALWAYS);

        HBox botoes = new HBox(10);
        botoes.setAlignment(Pos.CENTER);
        botoes.setStyle("-fx-padding: 10px;");

        Button novaFormula = new Button("+ Nova Fórmula");
        novaFormula.setStyle("-fx-font-size: 12px; -fx-padding: 8px 15px;");
        novaFormula.setOnAction(e -> {
            abrirDialogoFormula(null);
            listaFormulas.setAll(formulas);
        });

        Button fechar = new Button("Fechar");
        fechar.setStyle("-fx-font-size: 12px; -fx-padding: 8px 15px;");
        fechar.setOnAction(e -> dialogo.setResult(ButtonType.CLOSE));

        botoes.getChildren().addAll(novaFormula, fechar);

        conteudo.getChildren().addAll(tabelaFormulas, botoes);
        dialogo.getDialogPane().setContent(conteudo);
        dialogo.getDialogPane().setPrefSize(600, 400);

        dialogo.showAndWait();
    }

    private void abrirDialogoFormula(Formula formulaExistente) {
        boolean editando = formulaExistente != null;
        Dialog<ButtonType> dialogo = new Dialog<>();
        dialogo.setTitle(editando ? "Editar Fórmula" : "Nova Fórmula");
        dialogo.setResizable(true);

        TextField nomeFormula = new TextField();
        nomeFormula.setPromptText("Nome da fórmula (ex: Preparação A)");
        nomeFormula.setStyle("-fx-font-size: 12px; -fx-padding: 8px;");
        if (editando) {
            nomeFormula.setText(formulaExistente.getNome());
        }

        VBox conteudo = new VBox(10);
        conteudo.setPadding(new Insets(20));
        conteudo.setPrefSize(700, 500);

        Label labelNome = new Label("Nome da Fórmula:");
        labelNome.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        conteudo.getChildren().addAll(labelNome, nomeFormula);

        ObservableList<IngredienteFormula> listaIngredientes = FXCollections.observableArrayList();
        if (editando) {
            listaIngredientes.setAll(formulaExistente.getIngredientes());
        }

        TableView<IngredienteFormula> tabelaIngredientes = new TableView<>(listaIngredientes);
        tabelaIngredientes.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<IngredienteFormula, String> colProduto = new TableColumn<>("Produto");
        colProduto.setCellValueFactory(new PropertyValueFactory<>("produtoNome"));

        TableColumn<IngredienteFormula, Double> colQtd = new TableColumn<>("Quantidade (L)");
        colQtd.setCellValueFactory(new PropertyValueFactory<>("quantidade"));
        colQtd.setMaxWidth(120);

        TableColumn<IngredienteFormula, Void> colRemover = new TableColumn<>("Ação");
        colRemover.setMinWidth(100);
        colRemover.setCellFactory(param -> new TableCell<IngredienteFormula, Void>() {
            private final Button remover = new Button("Remover");

            {
                remover.setStyle("-fx-padding: 5px; -fx-font-size: 11px;");
                remover.setOnAction(e -> {
                    int index = this.getIndex();
                    if (index >= 0 && index < this.getTableView().getItems().size()) {
                        this.getTableView().getItems().remove(index);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic((empty || this.getIndex() < 0) ? null : remover);
                setAlignment(Pos.CENTER);
            }
        });

        tabelaIngredientes.getColumns().addAll(colProduto, colQtd, colRemover);
        VBox.setVgrow(tabelaIngredientes, Priority.ALWAYS);

        HBox adicionarIngrediente = new HBox(10);
        adicionarIngrediente.setStyle("-fx-border-top: 1px solid #cccccc; -fx-padding: 10px 0 0 0;");

        ComboBox<Produto> comboProdutos = new ComboBox<>(estoque);
        comboProdutos.setPrefWidth(250);
        comboProdutos.setCellFactory(lv -> new ListCell<Produto>() {
            @Override
            protected void updateItem(Produto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : String.format("%s (%.2f L)", item.getNome(), item.getQuantidade()));
            }
        });
        comboProdutos.setButtonCell(new ListCell<Produto>() {
            @Override
            protected void updateItem(Produto item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "Selecione um produto..." : String.format("%s (%.2f L)", item.getNome(), item.getQuantidade()));
            }
        });

        TextField qtdIngrediente = new TextField();
        qtdIngrediente.setPromptText("Quantidade (L)");
        qtdIngrediente.setPrefWidth(100);
        qtdIngrediente.setStyle("-fx-padding: 8px;");

        Button addBtn = new Button("Adicionar");
        addBtn.setStyle("-fx-padding: 8px 15px; -fx-font-size: 12px;");
        addBtn.setOnAction(e -> {
            Produto prod = comboProdutos.getValue();
            String qtdText = qtdIngrediente.getText().trim();
            
            if (prod == null) {
                mostrarErro("Selecione um produto");
                return;
            }
            
            if (qtdText.isEmpty()) {
                mostrarErro("Informe a quantidade");
                return;
            }
            
            try {
                double qtd = converterDouble(qtdText);
                if (qtd <= 0) {
                    mostrarErro("Quantidade deve ser maior que zero");
                    return;
                }
                IngredienteFormula ing = new IngredienteFormula(prod.getId(), prod.getNome(), qtd);
                listaIngredientes.add(ing);
                comboProdutos.setValue(null);
                qtdIngrediente.clear();
                comboProdutos.requestFocus();
            } catch (IllegalArgumentException erro) {
                mostrarErro("Quantidade inválida. Use ponto ou vírgula para decimais.");
            }
        });

        adicionarIngrediente.getChildren().addAll(
                new Label("Produto:"), comboProdutos,
                new Label("Qtd:"), qtdIngrediente,
                addBtn
        );
        adicionarIngrediente.setAlignment(Pos.CENTER_LEFT);
        adicionarIngrediente.setStyle("-fx-spacing: 5px;");

        Label labelIngredientes = new Label("Ingredientes:");
        labelIngredientes.setStyle("-fx-font-weight: bold; -fx-font-size: 12px;");
        
        conteudo.getChildren().addAll(
                labelIngredientes,
                tabelaIngredientes,
                adicionarIngrediente
        );

        ButtonType confirmar = new ButtonType("Salvar", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelar = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialogo.getDialogPane().setContent(conteudo);
        dialogo.getDialogPane().getButtonTypes().setAll(confirmar, cancelar);
        dialogo.getDialogPane().setPrefSize(700, 500);

        Optional<ButtonType> resultado = dialogo.showAndWait();

        if (resultado.isPresent() && resultado.get() == confirmar) {
            String nome = nomeFormula.getText().trim();
            
            if (nome.isEmpty()) {
                mostrarErro("Informe o nome da fórmula");
                return;
            }

            if (listaIngredientes.isEmpty()) {
                mostrarErro("Adicione pelo menos um ingrediente");
                return;
            }

            if (editando) {
                formulaExistente.setNome(nome);
                formulaExistente.getIngredientes().clear();
                formulaExistente.getIngredientes().addAll(listaIngredientes);
            } else {
                Formula novaFormula = new Formula(proximoIdFormula(), nome);
                novaFormula.getIngredientes().addAll(listaIngredientes);
                formulas.add(novaFormula);
            }

            salvarFormulas();
            mostrarMensagem("Fórmula salva com sucesso");
        }
    }

    private void aplicarFormula(Formula formula) {
        Alert confirma = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Deseja aplicar a fórmula \"" + formula.getNome() + "\"?\n" +
                "Isso irá subtrair os ingredientes do estoque.",
                ButtonType.YES,
                ButtonType.NO
        );

        confirma.setTitle("Aplicar Fórmula");
        Optional<ButtonType> resultado = confirma.showAndWait();

        if (resultado.isPresent() && resultado.get() == ButtonType.YES) {
            boolean sucesso = true;
            StringBuilder erros = new StringBuilder();
            int[] contador = {0};

            for (IngredienteFormula ing : formula.getIngredientes()) {
                Produto prod = estoque.stream()
                        .filter(p -> p.getId() == ing.getProdutoId())
                        .findFirst()
                        .orElse(null);

                if (prod == null) {
                    sucesso = false;
                    erros.append("Produto não encontrado: ").append(ing.getProdutoNome()).append("\n");
                } else if (!prod.removerQuantidade(ing.getQuantidade())) {
                    sucesso = false;
                    erros.append("Estoque insuficiente de ").append(ing.getProdutoNome()).append("\n");
                } else {
                    contador[0]++;
                }
            }

            if (sucesso) {
                salvarEstoque();
                final int quantidadeAplicada = contador[0];
                
                Platform.runLater(() -> {
                    tabela.refresh();
                    atualizarTabela();
                    mostrarMensagem("Fórmula aplicada com sucesso!\n" + quantidadeAplicada + " ingrediente(s) descontado(s)");
                });
            } else {
                Platform.runLater(() -> {
                    tabela.refresh();
                    mostrarErro("Erro ao aplicar fórmula:\n" + erros.toString());
                });
            }
        }
    }

    private void salvarFormulas() {
        try (ObjectOutputStream saida =
                     new ObjectOutputStream(
                             new FileOutputStream(ARQUIVO_FORMULAS))) {
            saida.writeObject(new ArrayList<>(formulas));
        } catch (IOException erro) {
            mostrarErro("Erro ao salvar as fórmulas.");
        }
    }

    @SuppressWarnings("unchecked")
    private void carregarFormulas() {
        File arquivo = new File(ARQUIVO_FORMULAS);

        if (!arquivo.exists()) {
            return;
        }

        try (ObjectInputStream entrada =
                     new ObjectInputStream(
                             new FileInputStream(arquivo))) {

            List<Formula> dados =
                    (List<Formula>) entrada.readObject();

            formulas.setAll(dados);

        } catch (IOException | ClassNotFoundException erro) {
            mostrarErro("Não foi possível carregar as fórmulas salvas.");
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}