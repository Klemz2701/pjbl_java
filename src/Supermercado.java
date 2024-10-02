import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

class SupermercadoException extends Exception {
    public SupermercadoException(String message) {
        super(message);
    }
}

abstract class ProdutoAbstrato implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;

    public ProdutoAbstrato(String nome) {
        this.nome = nome;
    }

    public String getNome() {
        return nome;
    }

    public abstract String exibirDetalhes();
}

class ProdutoNaoPerecivel extends ProdutoAbstrato {
    private static final long serialVersionUID = 1L;

    private double preco;
    private String marca;
    private String dataValidade;

    public ProdutoNaoPerecivel(String nome, double preco, String marca, String dataValidade) {
        super(nome);
        this.preco = preco;
        this.marca = marca;
        this.dataValidade = dataValidade;
    }

    @Override
    public String exibirDetalhes() {
        return "Produto Não Perecível" + "\nPreço: R$" + preco + "\nMarca: " + marca + "\nData de Validade: " + dataValidade;
    }
}

class ProdutoPerecivel extends ProdutoAbstrato {
    private static final long serialVersionUID = 1L;

    private double preco;
    private String marca;
    private String dataValidade;
    private int temperaturaArmazenamento;

    public ProdutoPerecivel(String nome, double preco, String marca, String dataValidade, int temperaturaArmazenamento) {
        super(nome);
        this.preco = preco;
        this.marca = marca;
        this.dataValidade = dataValidade;
        this.temperaturaArmazenamento = temperaturaArmazenamento;
    }

    @Override
    public String exibirDetalhes() {
        return "Produto Perecível"+ "\nPreço: R$" + preco + "\nMarca: " + marca + "\nData de Validade: " + dataValidade + "\nTemperatura de Armazenamento: " + temperaturaArmazenamento + "°C";
    }
}

class Cliente implements Serializable {
    private static final long serialVersionUID = 1L;

    private String nome;
    private String endereco;
    private String email;

    public Cliente(String nome, String endereco, String email) {
        this.nome = nome;
        this.endereco = endereco;
        this.email = email;
    }

    public String getNome() {
        return nome;
    }

    public String getEndereco() {
        return endereco;
    }

    public String getEmail() {
        return email;
    }

    public String exibirDetalhes() {
        return "Nome: " + nome + "\nEndereço: " + endereco + "\nEmail: " + email;
    }
}

class PersistenciaObjetos {
    public static void salvarObjeto(List<ProdutoAbstrato> listaProdutos, String arquivo) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(arquivo))) {
            oos.writeObject(listaProdutos);
            System.out.println("Produtos salvos com sucesso no arquivo " + arquivo);
        } catch (IOException e) {
            System.out.println("Erro ao salvar os produtos: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<ProdutoAbstrato> recuperarObjeto(String arquivo) {
        List<ProdutoAbstrato> listaProdutos = new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(arquivo))) {
            listaProdutos = (List<ProdutoAbstrato>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Erro ao recuperar os produtos: " + e.getMessage());
        }
        return listaProdutos;
    }

    public static void salvarClientes(List<Cliente> listaClientes, String arquivo) {
        try (PrintWriter writer = new PrintWriter(new FileWriter(arquivo))) {
            for (Cliente cliente : listaClientes) {
                writer.println(cliente.getNome() + "," + cliente.getEndereco() + "," + cliente.getEmail());
            }
            System.out.println("Clientes salvos com sucesso no arquivo " + arquivo);
        } catch (IOException e) {
            System.out.println("Erro ao salvar os clientes: " + e.getMessage());
        }
    }

    public static List<Cliente> recuperarClientes(String arquivo) {
        List<Cliente> listaClientes = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(arquivo))) {
            String linha;
            while ((linha = br.readLine()) != null) {
                String[] dados = linha.split(",");
                String nome = dados[0];
                String endereco = dados[1];
                String email = dados[2];

                Cliente cliente = new Cliente(nome, endereco, email);
                listaClientes.add(cliente);
            }
        } catch (IOException e) {
            System.out.println("Erro ao recuperar os clientes: " + e.getMessage());
        }
        return listaClientes;
    }
}

public class Supermercado {
    private List<ProdutoAbstrato> listaProdutos;
    private List<Cliente> listaClientes;
    private DefaultTableModel tableModelProdutos;
    private DefaultTableModel tableModelClientes;
    private JTable tabelaProdutos;
    private JTable tabelaClientes;
    private Cliente cliente;


    public Supermercado() {
        listaProdutos = PersistenciaObjetos.recuperarObjeto("produtos.txt");
        listaClientes = PersistenciaObjetos.recuperarClientes("clientes.txt");
        cliente = new Cliente("NomeCliente", "EnderecoCliente", "EmailCliente");

        JFrame frame = new JFrame("Supermercado");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(800, 600);

        JTabbedPane tabbedPane = new JTabbedPane();

        JPanel panelProdutos = new JPanel(new BorderLayout());
        tableModelProdutos = new DefaultTableModel(new Object[]{"Nome"}, 0);
        tabelaProdutos = new JTable(tableModelProdutos);
        atualizarTabelaProdutos();

        JScrollPane scrollPaneProdutos = new JScrollPane(tabelaProdutos);
        panelProdutos.add(scrollPaneProdutos, BorderLayout.CENTER);

        JButton adicionarProdutoButton = new JButton("Adicionar Produto");
        adicionarProdutoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    adicionarProduto();
                } catch (SupermercadoException ex) {
                    JOptionPane.showMessageDialog(null, ex.getMessage(), "Erro no Supermercado", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JButton retirarProdutoButton = new JButton("Retirar Produto");
        retirarProdutoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                retirarProduto();
            }
        });

        JButton salvarProdutosButton = new JButton("Salvar Produtos");
        salvarProdutosButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarProdutos();
            }
        });

        JButton detalhesProdutoButton = new JButton("Exibir Detalhes");
        detalhesProdutoButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exibirDetalhesProduto();
            }
        });

        JPanel panelBotoesProdutos = new JPanel();
        panelBotoesProdutos.add(adicionarProdutoButton);
        panelBotoesProdutos.add(retirarProdutoButton);
        panelBotoesProdutos.add(salvarProdutosButton);
        panelBotoesProdutos.add(detalhesProdutoButton);

        panelProdutos.add(panelBotoesProdutos, BorderLayout.SOUTH);

        tabbedPane.addTab("Produtos", panelProdutos);

        JPanel panelClientes = new JPanel(new BorderLayout());
        tableModelClientes = new DefaultTableModel(new Object[]{"Nome"}, 0);
        tabelaClientes = new JTable(tableModelClientes);
        atualizarTabelaClientes();

        JScrollPane scrollPaneClientes = new JScrollPane(tabelaClientes);
        panelClientes.add(scrollPaneClientes, BorderLayout.CENTER);

        JButton adicionarClienteButton = new JButton("Adicionar Cliente");
        adicionarClienteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                adicionarCliente();
            }
        });

        JButton retirarClienteButton = new JButton("Retirar Cliente");
        retirarClienteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                retirarCliente();
            }
        });

        JButton salvarClientesButton = new JButton("Salvar Clientes");
        salvarClientesButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                salvarClientes();
            }
        });

        JButton detalhesClienteButton = new JButton("Exibir Detalhes");
        detalhesClienteButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                exibirDetalhesCliente();
            }
        });

        JPanel panelBotoesClientes = new JPanel();
        panelBotoesClientes.add(adicionarClienteButton);
        panelBotoesClientes.add(retirarClienteButton);
        panelBotoesClientes.add(salvarClientesButton);
        panelBotoesClientes.add(detalhesClienteButton);

        panelClientes.add(panelBotoesClientes, BorderLayout.SOUTH);

        tabbedPane.addTab("Clientes", panelClientes);

        frame.add(tabbedPane);
        frame.setVisible(true);
    }

    private void atualizarTabelaProdutos() {
        tableModelProdutos.setRowCount(0);
        for (ProdutoAbstrato produto : listaProdutos) {
            tableModelProdutos.addRow(new Object[]{produto.getNome()});
        }
    }

    private void adicionarProduto() throws SupermercadoException {
        String nome = JOptionPane.showInputDialog("Digite o nome do produto:");
        if (nome != null) {
            try {
                double preco = Double.parseDouble(JOptionPane.showInputDialog("Digite o preço do produto:"));
                String marca = JOptionPane.showInputDialog("Digite a marca do produto:");
                String dataValidade = JOptionPane.showInputDialog("Digite a data de validade do produto:");

                // Perguntar se o produto é perecível
                String[] opcoes = {"Perecível", "Não Perecível"};
                int escolha = JOptionPane.showOptionDialog(null, "O produto é perecível?", "Tipo de Produto",
                        JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, opcoes, opcoes[0]);

                ProdutoAbstrato novoProduto;
                if (escolha == 0) {
                    int temperaturaArmazenamento = Integer.parseInt(JOptionPane.showInputDialog("Digite a temperatura de armazenamento do produto:"));
                    novoProduto = new ProdutoPerecivel(nome, preco, marca, dataValidade, temperaturaArmazenamento);
                } else {
                    novoProduto = new ProdutoNaoPerecivel(nome, preco, marca, dataValidade);
                }

                listaProdutos.add(novoProduto);
                atualizarTabelaProdutos();
                PersistenciaObjetos.salvarObjeto(listaProdutos, "produtos.txt");
            } catch (NumberFormatException e) {
                throw new SupermercadoException("Digite um valor válido para o preço.");
            }
        }
    }

    private void retirarProduto() {
        int linhaSelecionada = tabelaProdutos.getSelectedRow();
        if (linhaSelecionada != -1) {
            ProdutoAbstrato produtoRemovido = listaProdutos.remove(linhaSelecionada);
            atualizarTabelaProdutos();
            PersistenciaObjetos.salvarObjeto(listaProdutos, "produtos.txt");
        } else {
            JOptionPane.showMessageDialog(null, "Selecione um produto para retirar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void salvarProdutos() {
        PersistenciaObjetos.salvarObjeto(listaProdutos, "produtos.txt");
        JOptionPane.showMessageDialog(null, "Produtos salvos com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exibirDetalhesProduto() {
        int linhaSelecionada = tabelaProdutos.getSelectedRow();
        if (linhaSelecionada != -1) {
            ProdutoAbstrato produtoSelecionado = listaProdutos.get(linhaSelecionada);
            JOptionPane.showMessageDialog(null, produtoSelecionado.exibirDetalhes(), "Detalhes do Produto", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Selecione um produto para exibir detalhes.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void atualizarTabelaClientes() {
        tableModelClientes.setRowCount(0);
        for (Cliente cliente : listaClientes) {
            tableModelClientes.addRow(new Object[]{cliente.getNome()});
        }
    }

    private void adicionarCliente() {
        String nome = JOptionPane.showInputDialog("Digite o nome do cliente:");
        if (nome != null) {
            String endereco = JOptionPane.showInputDialog("Digite o endereço do cliente:");
            String email = JOptionPane.showInputDialog("Digite o email do cliente:");

            Cliente novoCliente = new Cliente(nome, endereco, email);
            listaClientes.add(novoCliente);
            atualizarTabelaClientes();
            PersistenciaObjetos.salvarClientes(listaClientes, "clientes.txt");
        }
    }
    private void retirarCliente() {
        int linhaSelecionada = tabelaClientes.getSelectedRow();
        if (linhaSelecionada != -1) {
            Cliente clienteRemovido = listaClientes.remove(linhaSelecionada);
            atualizarTabelaClientes();
            PersistenciaObjetos.salvarClientes(listaClientes, "clientes.txt");
        } else {
            JOptionPane.showMessageDialog(null, "Selecione um cliente para retirar.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void salvarClientes() {
        PersistenciaObjetos.salvarClientes(listaClientes, "clientes.txt");
        JOptionPane.showMessageDialog(null, "Clientes salvos com sucesso!", "Sucesso", JOptionPane.INFORMATION_MESSAGE);
    }

    private void exibirDetalhesCliente() {
        int linhaSelecionada = tabelaClientes.getSelectedRow();
        if (linhaSelecionada != -1) {
            Cliente clienteSelecionado = listaClientes.get(linhaSelecionada);
            JOptionPane.showMessageDialog(null, clienteSelecionado.exibirDetalhes(), "Detalhes do Cliente", JOptionPane.INFORMATION_MESSAGE);
        } else {
            JOptionPane.showMessageDialog(null, "Selecione um cliente para exibir detalhes.", "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new Supermercado();
            }
        });
    }
}

//