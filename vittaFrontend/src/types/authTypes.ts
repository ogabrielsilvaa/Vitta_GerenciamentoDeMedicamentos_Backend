import { UsuarioResponse } from "./usuarioTypes";

export type UsuarioLoginData = {
  email: string;
  senha: string;
}

export type LoginResponse = {
  token: string;
  usuario: UsuarioResponse;
}

export type UsuarioCadastroData = {
  nome: string;
  telefone: string;
  email: string;
  senha: string;
}

export type CadastroResponse = {
  token: string;
  usuario: UsuarioResponse;
}
