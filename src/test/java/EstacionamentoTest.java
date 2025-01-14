import br.ufc.quixada.poo.*;
import org.junit.jupiter.api.*;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class EstacionamentoTest {

  Estacionamento estacionamento;

  @BeforeEach
  void setUp() {
    estacionamento = new Estacionamento(2, 3);
  }

  @Test
  void deveRegistrarEntradaDeCarro() {
    Veiculo carro = new Carro("CAR001");
    assertTrue(estacionamento.registrarEntrada(carro), "Deveria ter permitido a entrada do carro");
    assertEquals(1, estacionamento.vagasDisponiveisPara(TipoVaga.CARRO), "Apenas um vaga para carro deveria estar disponivel");
  }

  @Test
  void deveRegistrarEntradaDeMotoEBike() {
    Veiculo moto = new Moto("MOTO001");
    Veiculo bike = new Bike("BIKE001");

    assertTrue(estacionamento.registrarEntrada(moto), "Deveria ter permitido a entrada da moto");
    assertTrue(estacionamento.registrarEntrada(bike), "Deveria ter permitido a entrada da bike");
    assertEquals(1, estacionamento.vagasDisponiveisPara(TipoVaga.MOTO_E_BIKE), "Apenas um vaga para moto e bike deveria estar disponivel");
  }

  @Test
  void naoDevePermitirVeiculosDuplicados() {
    Veiculo carro = new Carro("CAR001");
    estacionamento.registrarEntrada(carro);
    assertFalse(estacionamento.registrarEntrada(carro), "Não deve permitir o registro de veículos duplicados.");
  }

  @Test
  void naoDeveRegistrarEntradaQuandoEstacionamentoLotado() {
    Veiculo carro1 = new Carro("CAR001");
    Veiculo carro2 = new Carro("CAR002");
    Veiculo carro3 = new Carro("CAR003");

    assertTrue(estacionamento.registrarEntrada(carro1), "Deveria ter permitido a entrada do carro");
    assertTrue(estacionamento.registrarEntrada(carro2), "Deveria ter permitido a entrada do carro");
    assertFalse(estacionamento.registrarEntrada(carro3), "Vagas de carro lotadas, nenhum veiculo pode entrar");

  }

  @Test
  void deveCalcularValorDeMotoComMinimoDe3Reais() {
    Veiculo moto = new Moto("MOTO001");
    estacionamento.registrarEntrada(moto);

    Ticket ticket = estacionamento.getTicketBy("MOTO001");
    estacionamento.registrarSaida("MOTO001", ticket.getHoraEntrada().plusMinutes(1));

    assertNotNull(ticket);
    assertEquals(3.0, ticket.getValorPago(), 0.001, "Valor incorreto");
  }

  @Test
  void deveCalcularValorDeCarroComMinimoDe5Reais() {
    Veiculo carro = new Carro("CAR001");
    estacionamento.registrarEntrada(carro);

    Ticket ticket = estacionamento.getTicketBy("CAR001");
    estacionamento.registrarSaida("CAR001", ticket.getHoraEntrada().plusMinutes(10));

    assertNotNull(ticket);
    assertEquals(5.0, ticket.getValorPago(), 0.001, "Valor incorreto");
  }

  @Test
  void deveLiberarVagaAposPagamento() {
    Veiculo carro = new Carro("CAR001");
    estacionamento.registrarEntrada(carro);
    estacionamento.registrarSaida("CAR001", LocalDateTime.now());
    assertEquals(2, estacionamento.vagasDisponiveisPara(TipoVaga.CARRO), "A vaga antes ocupada deveria ter sido liberada.");

    Veiculo bike = new Bike("BIKE001");
    estacionamento.registrarEntrada(bike);
    estacionamento.registrarSaida("BIKE001", LocalDateTime.now());
    assertEquals(3, estacionamento.vagasDisponiveisPara(TipoVaga.MOTO_E_BIKE), "A vaga antes ocupada deveria ter sido liberada.");
  }

  @Test
  void deveListarVeiculosEstacionados() {
    Veiculo carro = new Carro("CAR001");
    Veiculo moto = new Moto("MOTO001");
    Veiculo[] veiculos = {carro, moto};
    estacionamento.registrarEntrada(carro);
    estacionamento.registrarEntrada(moto);

    Veiculo[] estacionados = estacionamento.listarVeiculosEstacionados();
    assertEquals(2, estacionados.length);
    for (int i = 0; i < veiculos.length; i++) {
      assertEquals(veiculos[i].getIdentificador(), estacionados[i].getIdentificador());
    }
  }

  @Test
  void deveRegistrarSaidaECalcularValorCorretamente() {
    Veiculo carro = new Carro("CAR001");
    estacionamento.registrarEntrada(carro);

    Ticket ticket = estacionamento.getTicketBy("CAR001");
    estacionamento.registrarSaida("CAR001", ticket.getHoraEntrada().plusHours(5));

    assertNotNull(ticket);
    assertTrue(ticket.isPago(), "O ticket deve ser marcado como pago");
    assertEquals(30.0, ticket.getValorPago(), 0.01, "Valor incorreto");
  }

  @Test
  void naoDevePermitirPagamentoDeTicketInexistente() {
    assertFalse(estacionamento.registrarSaida("INVALID001", LocalDateTime.now()),"Ticket não encontrado para o identificador: INVALID001");
  }

  @Test
  void naoDevePermitirRegistrarSaidaJaPago() {
    Veiculo carro = new Carro("CAR001");
    estacionamento.registrarEntrada(carro);
    estacionamento.registrarSaida("CAR001", LocalDateTime.now());

    assertFalse(estacionamento.registrarSaida("CAR001", LocalDateTime.now()),"Ticket já foi pago.");

  }

  @Test
  void devePoderVoltarAposPagamento() {
    Veiculo carro = new Carro("CAR001");
    estacionamento.registrarEntrada(carro);
    estacionamento.registrarSaida("CAR001", LocalDateTime.now());
    assertEquals(2, estacionamento.vagasDisponiveisPara(TipoVaga.CARRO));
    assertTrue(estacionamento.registrarEntrada(carro), "Um veiculo pode estacionar varias vezes durante o dia");

  }

  @Test
  void deveCalcularValorParaBikesComPermanenciaLonga() {
    Veiculo bike = new Bike("BIKE001");
    estacionamento.registrarEntrada(bike);

    Ticket ticket = estacionamento.getTicketBy("BIKE001");
    estacionamento.registrarSaida("BIKE001", ticket.getHoraEntrada().plusMinutes(120));

    assertEquals(3.0, ticket.getValorPago(), 0.01, "Valor incorreto");
  }

  @Test
  void deveCalcularValorParaMotosComPermanenciaCurta() {
    Veiculo moto = new Moto("MOTO001");
    estacionamento.registrarEntrada(moto);

    Ticket ticket = estacionamento.getTicketBy("MOTO001");
    estacionamento.registrarSaida("MOTO001", ticket.getHoraEntrada().plusMinutes(30));

    assertEquals(3.0, ticket.getValorPago(), 0.01, "Valor incorreto");  // Mínimo de 3 reais
  }

  @Test
  void deveImpedirRegistrarEntradaDeVeiculosQuandoEstacionamentoLotado() {
    estacionamento.registrarEntrada(new Moto("MOTO001"));
    estacionamento.registrarEntrada(new Moto("MOTO002"));
    estacionamento.registrarEntrada(new Moto("MOTO003"));

    assertFalse(estacionamento.registrarEntrada(new Moto("MOTO004")), "Não deve permitir entrada quando todas as vagas estão ocupadas.");
  }
}
