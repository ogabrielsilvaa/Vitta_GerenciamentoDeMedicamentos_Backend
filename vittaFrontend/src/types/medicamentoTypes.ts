import { GeralStatus } from "../enums/geralStatus";
import { TipoUnidadeDeMedida } from "../enums/medicamento/tipoUnidadeDeMedida";

export type MedicamentoCadastroData = {
  nome: string;
  principioAtivo: string;
  laboratorio: string;
  tipoUnidadeDeMedida: TipoUnidadeDeMedida;
}

export type MedicamentoAtualizarData = {
  nome: string;
  principioAtivo: string;
  laboratorio: string;
  tipoUnidadeDeMedida: TipoUnidadeDeMedida;
}

export type MedicamentoResponse = {
  id: number;
  nome: string;
  principioAtivo: string;
  laboratorio: string;
  tipoUnidadeDeMedida: TipoUnidadeDeMedida;
}

export type MedicamentoResumoResponse = {
  id: number;
  nome: string;
  status: GeralStatus;
}