import { UsuarioStatus } from "../enums/usuario/usuarioStatus";
import { TratamentoResumoResponse } from "./tratamentoTypes";

export type UsuarioResponse = {
  id: number;
  nome: string;
  telefone: string;
  email: string;
  status: UsuarioStatus;
  tratamentos?: TratamentoResumoResponse[];
}

export type UsuarioResumoResponse = {
  id: number;
  nome: string;
  status: UsuarioStatus
}
