import axios from "axios";
import { AgendamentoResponse, RegistrarUsoRequest } from "../types/agendamentoTypes";
import { MedicamentoHistoricoResumoResponse } from "../types/medicamentoHistoricoTypes";
import apiManager from "./apiManager";

export function formatarData(data: Date): string {
  const ano = data.getFullYear();
  const mes = String(data.getMonth() + 1).padStart(2, '0');
  const dia = String(data.getDate()).padStart(2, '0');

  return `${ano}-${mes}-${dia}`;
}

export const listarAgendamentos = async (
  token: string,
  dataInicio?: string,
  dataFim?: string
): Promise<AgendamentoResponse[]> => {
  try {
    const response = await apiManager.get<AgendamentoResponse[]>('api/agendamentos/listar', {

      headers: {
        'Authorization': `Bearer ${token}`
      },

      params: {
        dataInicio: dataInicio,
        dataFim: dataFim
      }
    });

    return response.data;
  } catch (error) {
    if (axios.isAxiosError(error)) {
      console.error(`Erro ao listar agendamentos: ${error.message}`, error.response?.data);
    } else {
      console.error(`Erro ao listar agendamentos para o token: ${token}`, error);
    }

    throw error;
  }
};

export const concluirAgendamento = async (
  token: string,
  agendamentoId: number,
  dadosUso: RegistrarUsoRequest
): Promise<MedicamentoHistoricoResumoResponse> => {
  try {
    const response = await apiManager.post<MedicamentoHistoricoResumoResponse>(
      `api/agendamentos/concluirAgendamento/${agendamentoId}`, 
      dadosUso,
      {
        headers: {
          'Authorization': `Bearer ${token}`
        }
      }
    );

    return response.data;

  } catch (error) {
    if (axios.isAxiosError(error)) {
      console.error(`Erro ao concluir agendamento: ${error.message}`, error.response?.data);
    } else {
      console.error(`Erro ao concluir agendamento para o ID: ${agendamentoId}`, error);
    }
    throw error;
  }
};
