import { TipoDeAlerta } from "../enums/agendamento/tipoDeAlerta";
import { TipoDeFrequencia } from "../enums/tratamento/tipoDeFrequencia";
import { TratamentoStatus } from "../enums/tratamento/tratamentoStatus";
import { AgendamentoResumoResponse } from "./agendamentoTypes";
import { MedicamentoResumoResponse } from "./medicamentoTypes";
import { UsuarioResumoResponse } from "./usuarioTypes";

export type TratamentoCadastroData = {
  nome: string;
  dosagem: number;
  instrucoes: string;
  dataDeInicio: string;
  dataDeTermino: string;
  tipoDeFrequencia: TipoDeFrequencia;
  intervaloEmHoras: number;
  horariosEspecificos: string;
  tipoDeAlerta: TipoDeAlerta
  medicamentoId: number;
}

export type TratamentoAtualizarData = {
  nome: string;
  dosagem: number;
  instrucoes: string;
  dataDeInicio: string;
  dataDeTermino: string;
  tipoDeFrequencia: TipoDeFrequencia;
  intervaloEmHoras: number;
  horariosEspecificos: string;
  tipoDeAlerta: TipoDeAlerta
}

export type TratamentoResponse = {
  id: number;
  nome: string;
  dosagem: number;
  instrucoes: string;
  dataDeInicio: string;
  dataDeTermino: string;
  tipoDeFrequencia: string;
  intervaloEmHoras: number;
  horariosEspecificos: string;
  tipoDeAlerta: string
  status: TratamentoStatus;
  agendamentos: AgendamentoResumoResponse[];
  usuario: UsuarioResumoResponse;
  medicamento: MedicamentoResumoResponse;
}

export type TratamentoResumoResponse = {
  id: number;
  nome: string;
  dataDeInicio: string;
  dataDeTermino: string;
  status: TratamentoStatus;
  medicamento: MedicamentoResumoResponse;
}
