import axios from "axios";
import { MedicamentoAtualizarData, MedicamentoCadastroData, MedicamentoResponse, MedicamentoResumoResponse } from "../types/medicamentoTypes";
import apiManager from "./apiManager";

export const listarMedicamentos = async (
  token: string,
): Promise<MedicamentoResponse[]> => {
  try {
    const response = await apiManager.get<MedicamentoResponse[]>('api/medicamentos/listar', {

      headers: {
        'Authorization': `Bearer ${token}`
      },

    });

    return response.data;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      console.error(`Erro ao listar medicamentos: ${error.message}`, error.response?.data);
    } else {
      console.error(`Erro ao listar medicamentos para o token: ${token}`, error);
    }

    throw error;
  }
}

export const listarMedicamentosResumo = async (
  token: string,
): Promise<MedicamentoResumoResponse[]> => {
  try {
    const response = await apiManager.get<MedicamentoResumoResponse[]>('api/medicamentos/listar', {

      headers: {
        'Authorization': `Bearer ${token}`
      },

    });

    return response.data;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      console.error(`Erro ao listar medicamentos: ${error.message}`, error.response?.data);
    } else {
      console.error(`Erro ao listar medicamentos para o token: ${token}`, error);
    }

    throw error;
  }
}

export const listarUnicoMedicamento = async (
  token: string,
  medicamentoId: number,
): Promise<MedicamentoResponse> => {
  try {
    const response = await apiManager.get<MedicamentoResponse>(`api/medicamentos/listarMedicamentoPorId/${medicamentoId}`, {

      headers: {
        'Authorization': `Bearer ${token}`
      },

    });

    return response.data;
  } catch (error) {
    console.error("Erro na chamada para listar medicamento por ID: ", error);
    throw error;
  }
}


export const medicamentoCadastro = async (
  token: string,
  data: MedicamentoCadastroData ): Promise<MedicamentoResponse> => {
  try {
    const response = await apiManager.post<MedicamentoResponse>('api/medicamentos/cadastrar', data, {

      headers: {
        'Authorization': `Bearer ${token}`
      },

    });

    return response.data;
  } catch (error) {
    console.error("Erro na chamada de cadastro: ", error);
    throw error;
  }
}


export const medicamentoAtualizar = async (
  token: string,
  medicamentoId: number,
  data: MedicamentoAtualizarData): Promise<MedicamentoResponse> => {
  try {
    const response = await apiManager.put<MedicamentoResponse>(`api/medicamentos/atualizar/${medicamentoId}`, data, {
      
      headers: {
        'Authorization': `Bearer ${token}`
      },

      data,
    });
    return response.data;
  } catch (error) {
    console.error("Erro na chamada para atualizar medicamento: ", error);
    throw error;
  }
}


export const medicamentoDeletar = async (
  token: string,
  medicamentoId: number,
): Promise<MedicamentoResponse> => {
  try {
    const response = await apiManager.delete<MedicamentoResponse>(`api/medicamentos/deletar/${medicamentoId}`, {

      headers: {
        'Authorization': `Bearer ${token}`
      },

    });

    return response.data;
  } catch (error) {
    console.error("Erro na chamada parar deletar medicamento: ", error);
    throw error;
  }
}
