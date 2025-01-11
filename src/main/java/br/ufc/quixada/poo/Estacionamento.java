package br.ufc.quixada.poo;

import java.util.ArrayList;
import java.util.List;

public class Estacionamento {


  public Estacionamento(int vagasCarro, int vagasMotoBike) {
  }

  public boolean registrarEntrada(Veiculo veiculo) {
    return false;
  }

  public boolean registrarSaida(String identificador, int minutosPassados) {
    return false;
  }

  public Ticket getTicketBy(String identificador) {
    return null;
  }

  public Veiculo[] listarVeiculosEstacionados() {
    return null;
  }

  public int vagasDisponiveisPara(TipoVaga tipo) {
    return -1;
  }
}

