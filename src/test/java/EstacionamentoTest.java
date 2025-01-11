import br.ufc.quixada.poo.*;
import org.junit.jupiter.api.*;

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
    assertTrue(estacionamento.registrarEntrada(carro));
    assertEquals(1, estacionamento.vagasDisponiveisPara(TipoVaga.CARRO));
  }

  @Test
  void deveRegistrarEntradaDeMotoEBike() {
    Veiculo moto = new Moto("MOTO001");
    Veiculo bike = new Bike("BIKE001");

    assertTrue(estacionamento.registrarEntrada(moto));
    assertTrue(estacionamento.registrarEntrada(bike));
    assertEquals(1, estacionamento.vagasDisponiveisPara(TipoVaga.MOTO_E_BIKE));
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

    assertTrue(estacionamento.registrarEntrada(carro1));
    assertTrue(estacionamento.registrarEntrada(carro2));
    assertFalse(estacionamento.registrarEntrada(carro3));
  }

  @Test
  void deveCalcularValorDeMotoComMinimoDe3Reais() {
    Veiculo moto = new Moto("MOTO001");
    estacionamento.registrarEntrada(moto);

    estacionamento.registrarSaida("MOTO001", 5);
    Ticket ticket = estacionamento.getTicketBy("MOTO001");

    assertNotNull(ticket);
    assertEquals(3.0, ticket.getValorPago(), 0.01);
  }

  @Test
  void deveCalcularValorDeCarroComMinimoDe5Reais() {
    Veiculo carro = new Carro("CAR001");
    estacionamento.registrarEntrada(carro);

    estacionamento.registrarSaida("CAR001", 10);
    Ticket ticket = estacionamento.getTicketBy("CAR001");

    assertNotNull(ticket);
    assertTrue(ticket.getValorPago() >= 5.0);
  }

  @Test
  void deveLiberarVagaAposPagamento() {
    Veiculo carro = new Carro("CAR001");
    estacionamento.registrarEntrada(carro);
    estacionamento.registrarSaida("CAR001", 120);
    assertEquals(2, estacionamento.vagasDisponiveisPara(TipoVaga.CARRO));
  }

  @Test
  void deveListarVeiculosEstacionados() {
    Veiculo carro = new Carro("CAR001");
    Veiculo moto = new Moto("MOTO001");
    estacionamento.registrarEntrada(carro);
    estacionamento.registrarEntrada(moto);

    assertEquals(2, estacionamento.listarVeiculosEstacionados().length);
  }

  @Test
  void deveRegistrarSaidaECalcularValorCorretamente() {
    Veiculo carro = new Carro("CAR001");
    estacionamento.registrarEntrada(carro);

    estacionamento.registrarSaida("CAR001", 300);
    Ticket ticket = estacionamento.getTicketBy("CAR001");

    assertNotNull(ticket);
    assertTrue(ticket.isPago());
    assertTrue(ticket.getValorPago() >= 5.0);
  }

  @Test
  void naoDevePermitirPagamentoDeTicketInexistente() {
    assertFalse(estacionamento.registrarSaida("INVALID001", 120),"Ticket não encontrado para o identificador: INVALID001");
  }

  @Test
  void naoDevePermitirRegistrarSaidaJaPago() {
    Veiculo carro = new Carro("CAR001");
    estacionamento.registrarEntrada(carro);
    estacionamento.registrarSaida("CAR001", 120);

    assertFalse(estacionamento.registrarSaida("CAR001", 120),"Ticket já foi pago.");

  }

  @Test
  void devePoderVoltarAposPagamento() {
    Veiculo carro = new Carro("CAR001");
    estacionamento.registrarEntrada(carro);
    estacionamento.registrarSaida("CAR001", 120);
    assertEquals(2, estacionamento.vagasDisponiveisPara(TipoVaga.CARRO));
    assertTrue(estacionamento.registrarEntrada(carro), "Um veiculo pode estacionar varias vezes durante o dia");

  }

  @Test
  void deveCalcularValorParaCarrosComPermanenciaLonga() {
    Veiculo carro = new Carro("CAR001");
    estacionamento.registrarEntrada(carro);

    Ticket ticket = estacionamento.getTicketBy("CAR001");
    estacionamento.registrarSaida("CAR001", 120);

    assertEquals(12.0, ticket.getValorPago(), 0.01);
  }

  @Test
  void deveCalcularValorParaMotosComPermanenciaCurta() {
    Veiculo moto = new Moto("MOTO001");
    estacionamento.registrarEntrada(moto);

    Ticket ticket = estacionamento.getTicketBy("MOTO001");
    estacionamento.registrarSaida("MOTO001", 30);

    assertEquals(3.0, ticket.getValorPago(), 0.01);  // Mínimo de 3 reais
  }

  @Test
  void deveImpedirRegistrarEntradaDeVeiculosQuandoEstacionamentoLotado() {
    estacionamento.registrarEntrada(new Moto("MOTO001"));
    estacionamento.registrarEntrada(new Moto("MOTO002"));
    estacionamento.registrarEntrada(new Moto("MOTO003"));

    assertFalse(estacionamento.registrarEntrada(new Moto("MOTO004")), "Não deve permitir entrada quando todas as vagas estão ocupadas.");
  }
}
