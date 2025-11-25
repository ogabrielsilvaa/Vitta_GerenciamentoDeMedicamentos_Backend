import { AgendamentoStatus } from "../enums/agendamento/agendamentoStatus";
import { TipoDeAlerta } from "../enums/agendamento/tipoDeAlerta";
import { MedicamentoHistoricoResumoResponse } from "./medicamentoHistoricoTypes";
import { TratamentoResponse } from "./tratamentoTypes";
import { UsuarioResumoResponse } from "./usuarioTypes";

export type AgendamentoResponse = {
  id: number;
  horarioDoAgendamento: Date;
  tipoDeAlerta: TipoDeAlerta;
  status: AgendamentoStatus;
  usuario: UsuarioResumoResponse;
  tratamento: TratamentoResponse;
  historicoDoMedicamentoTomado: MedicamentoHistoricoResumoResponse;
}

export type AgendamentoResumoResponse = {
  id: number;
  horarioDoAgendamento: Date;
  tipoDeAlerta: TipoDeAlerta;
  status: AgendamentoStatus;
}

export interface RegistrarUsoRequest {
  horaDoUso: string;
  doseTomada: number;
  observacao?: string;
}