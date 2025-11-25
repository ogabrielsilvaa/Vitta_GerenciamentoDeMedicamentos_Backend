import axios from "axios";
import { TratamentoAtualizarData, TratamentoCadastroData, TratamentoResponse } from "../types/tratamentoTypes";
import apiManager from "./apiManager";

export const listarTratamentos = async (
  token: string,
): Promise<TratamentoResponse[]> => {
  try {
    const response = await apiManager.get<TratamentoResponse[]>('api/tratamentos/listar', {

      headers: {
        'Authorization': `Bearer ${token}`
      },

    });

    return response.data;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      console.error(`Erro ao listar tratamentos: ${error.message}`, error.response?.data);
    } else {
      console.error(`Erro ao listar tratamentos para o token: ${token}`, error);
    }

    throw error;
  }
}

export const listarUnicoTratamento = async (
  token: string,
  tratamentoId: number,
): Promise<TratamentoResponse> => {
  try {
    const response = await apiManager.get<TratamentoResponse>(`api/tratamentos/listarTratamentoPorId/${tratamentoId}`, {

      headers: {
        'Authorization': `Bearer ${token}`
      },

    });

    return response.data;
  } catch (error) {
    console.error("Erro na chamada para listar tratamento por ID: ", error);
    throw error;
  }
}

export const tratamentoCadastro = async (
  token: string,
  data: TratamentoCadastroData ): Promise<TratamentoResponse> => {
  try {
    const response = await apiManager.post<TratamentoResponse>('api/tratamentos/cadastrar', data, {

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

export const tratamentoAtualizar = async (
  token: string,
  tratamentoId: number,
  data: TratamentoAtualizarData): Promise<TratamentoResponse> => {
  try {
    const response = await apiManager.put<TratamentoResponse>(`api/tratamentos/atualizar/${tratamentoId}`, data, {
      
      headers: {
        'Authorization': `Bearer ${token}`
      },

      data,
    });
    return response.data;
  } catch (error) {
    console.error("Erro na chamada para atualizar tratamento: ", error);
    throw error;
  }
}

export const tratamentoDeletar = async (
  token: string,
  tratamentoId: number,
): Promise<TratamentoResponse> => {
  try {
    const response = await apiManager.delete<TratamentoResponse>(`api/tratamentos/deletar/${tratamentoId}`, {

      headers: {
        'Authorization': `Bearer ${token}`
      },

    });

    return response.data;
  } catch (error) {
    console.error("Erro na chamada parar deletar tratamento: ", error);
    throw error;
  }
  
}

export const tratamentoEncerrar = async (
  token: string,
  tratamentoId: number,
): Promise<TratamentoResponse> => {
  try {
    const response = await apiManager.patch<TratamentoResponse>(
      `api/tratamentos/encerrar/${tratamentoId}`, 
      {},
      {
        headers: {
          'Authorization': `Bearer ${token}`
        },
      }
    );

    return response.data;
  } catch (error) {
    console.error("Erro na chamada parar encerrar tratamento: ", error);
    throw error;
  }
}
