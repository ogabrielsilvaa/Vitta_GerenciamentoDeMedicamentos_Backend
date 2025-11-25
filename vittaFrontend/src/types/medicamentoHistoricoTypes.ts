import { GeralStatus } from "../enums/geralStatus";

export type MedicamentoHistoricoResumoResponse = {
  id: number;
  horaDoUso: Date;
  doseTomada: number;
  observacao: string;
  historicoStatus: GeralStatus;
}
