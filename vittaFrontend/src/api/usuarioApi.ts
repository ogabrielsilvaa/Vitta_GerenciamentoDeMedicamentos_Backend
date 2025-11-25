import { CadastroResponse, LoginResponse, UsuarioCadastroData, UsuarioLoginData } from "@/src/types/authTypes";
import apiManager from "./apiManager";

export const usuarioLogin = async (data: UsuarioLoginData): Promise<LoginResponse> => {
  try {
    const response = await apiManager.post<LoginResponse>('api/usuarios/login', data);
    return response.data;
  } catch (error) {
    console.error("Erro na chamada de login: ", error);
    throw error;
  }
};

export const usuarioCadastro = async (data: UsuarioCadastroData): Promise<CadastroResponse> => {
  try {
    const response = await apiManager.post<CadastroResponse>('api/usuarios/cadastrar', data);
    return response.data;
  } catch (error) {
    console.error("Erro na chamada de cadastro: ", error);
    throw error;
  }
}
