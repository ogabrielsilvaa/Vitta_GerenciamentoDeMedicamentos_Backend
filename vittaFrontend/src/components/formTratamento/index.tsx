import { listarMedicamentosResumo } from "@/src/api/medicamentoApi";
import {
  tratamentoAtualizar,
  tratamentoCadastro,
} from "@/src/api/tratamentoApi";
import { useSession } from "@/src/contexts/authContext";
import { TipoDeAlerta } from "@/src/enums/agendamento/tipoDeAlerta";
import { TipoDeFrequencia } from "@/src/enums/tratamento/tipoDeFrequencia";
import { MedicamentoResumoResponse } from "@/src/types/medicamentoTypes";
import {
  TratamentoCadastroData,
  TratamentoResponse,
} from "@/src/types/tratamentoTypes";
import { useEffect, useState } from "react";
import { ActivityIndicator, ScrollView, Text, View } from "react-native";
import { KeyboardAwareScrollView } from "react-native-keyboard-aware-scroll-view";
import { ActionButton } from "../actionButton/actionButton";
import { DropdownInput } from "../dropdownInput";
import { FormDatePicker } from "../formDatePicker";
import { FormInput } from "../formInput";
import { styles } from "./styles";

const parseDateSafe = (data: any): Date | null => {
  if (!data) return null;

  if (data instanceof Date) {
    return isNaN(data.getTime()) ? null : data;
  }

  try {
    const dateString = String(data).split("T")[0];

    const [ano, mes, dia] = dateString.split("-").map(Number);

    return new Date(ano, mes - 1, dia, 12, 0, 0);
  } catch {
    return null;
  }
};

const formatDataApi = (date: Date) => {
  const ano = date.getFullYear();
  const mes = (date.getMonth() + 1).toString().padStart(2, "0");
  const dia = date.getDate().toString().padStart(2, "0");
  return `${ano}-${mes}-${dia}`;
};

type FormularioProps = {
  onClose: () => void;
  onSaveSuccess: () => void;
  tratamentoParaEditar?: TratamentoResponse | null;
};

export default function FormularioTratamento({
  onClose,
  onSaveSuccess,
  tratamentoParaEditar,
}: FormularioProps) {
  const { session } = useSession();

  const [etapa, setEtapa] = useState(1);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState<string | null>(null);
  const [listaMedicamentos, setListaMedicamentos] = useState<
    MedicamentoResumoResponse[]
  >([]);

  const [medIdSelecionado, setMedIdSelecionado] = useState<string>(
    tratamentoParaEditar?.medicamento?.id
      ? String(tratamentoParaEditar.medicamento.id)
      : ""
  );
  const [nome, setNome] = useState(tratamentoParaEditar?.nome ?? "");
  const [dosagem, setDosagem] = useState(
    tratamentoParaEditar?.dosagem ? String(tratamentoParaEditar.dosagem) : ""
  );
  const [instrucoes, setInstrucoes] = useState(
    tratamentoParaEditar?.instrucoes ?? ""
  );
  const [dataInicio, setDataInicio] = useState<Date | null>(
    parseDateSafe(tratamentoParaEditar?.dataDeInicio)
  );
  const [dataTermino, setDataTermino] = useState<Date | null>(
    parseDateSafe(tratamentoParaEditar?.dataDeTermino)
  );
  const [freqTipo, setFreqTipo] = useState<string>(
    tratamentoParaEditar?.tipoDeFrequencia
      ? String(tratamentoParaEditar.tipoDeFrequencia)
      : ""
  );
  const [intervalo, setIntervalo] = useState(
    tratamentoParaEditar?.intervaloEmHoras
      ? String(tratamentoParaEditar.intervaloEmHoras)
      : ""
  );
  const [horarios, setHorarios] = useState(
    tratamentoParaEditar?.horariosEspecificos ?? ""
  );
  const [alertaTipo, setAlertaTipo] = useState<string>(
    tratamentoParaEditar?.tipoDeAlerta
      ? String(tratamentoParaEditar.tipoDeAlerta)
      : ""
  );

  useEffect(() => {
    if (session) {
      listarMedicamentosResumo(session)
        .then(setListaMedicamentos)
        .catch(() => setErrorMessage("Erro ao carregar medicamentos."));
    }
  }, [session]);

  const validarEAvancar = () => {
    setErrorMessage(null);
    if (etapa === 1) {
      if (!medIdSelecionado)
        return setErrorMessage("Selecione um medicamento.");
      if (!nome.trim()) return setErrorMessage("Nome é obrigatório.");
      if (!dosagem.trim()) return setErrorMessage("Dosagem é obrigatória.");
      setEtapa(2);
    } else if (etapa === 2) {
      if (!dataInicio) return setErrorMessage("Data Início obrigatória.");
      if (!dataTermino) return setErrorMessage("Data Término obrigatória.");
      setEtapa(3);
    }
  };

  const handleSalvar = async () => {
    if (!freqTipo) return setErrorMessage("Selecione a frequência.");
    if (!alertaTipo) return setErrorMessage("Selecione o alerta.");

    setIsLoading(true);
    try {
      const freqEnum = Number(freqTipo);
      const payload: TratamentoCadastroData = {
        nome,
        dosagem: parseFloat(dosagem.replace(",", ".")),
        instrucoes,
        dataDeInicio: formatDataApi(dataInicio!),
        dataDeTermino: formatDataApi(dataTermino!),
        tipoDeFrequencia: freqEnum,
        intervaloEmHoras:
          freqEnum === TipoDeFrequencia.INTERVALO_HORAS
            ? parseInt(intervalo)
            : 0,
        horariosEspecificos:
          freqEnum === TipoDeFrequencia.HORARIOS_ESPECIFICOS ? horarios : "",
        tipoDeAlerta: Number(alertaTipo),
        medicamentoId: Number(medIdSelecionado),
      };

      if (tratamentoParaEditar) {
        await tratamentoAtualizar(session!, tratamentoParaEditar.id, payload);
      } else {
        await tratamentoCadastro(session!, payload);
      }
      onSaveSuccess();
      onClose();
    } catch (e) {
      console.error(e);
      setErrorMessage("Erro ao salvar.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <KeyboardAwareScrollView
      style={{ flex: 1 }}
      resetScrollToCoords={{ x: 0, y: 0 }}
      contentContainerStyle={[
        styles.modalContent,
        { flexGrow: 1, justifyContent: "center", alignItems: "center" },
      ]}
      scrollEnabled={true}
      enableOnAndroid={true}
      bounces={false}
    >
      <View style={styles.formBody}>
        <ScrollView
          style={styles.formRequestBody}
          keyboardShouldPersistTaps="handled"
        >
          {errorMessage && (
            <View style={styles.errorContainer}>
              <Text style={styles.errorText}>{errorMessage}</Text>
            </View>
          )}

          {etapa === 1 && (
            <>
              <FormInput
                titulo="Nome"
                value={nome}
                onChangeText={setNome}
                placeHolder="Ex: Tratamento Gripe"
              />

              <DropdownInput
                label="Medicamento"
                placeholder="Selecione o medicamento..."
                items={listaMedicamentos.map((m) => ({
                  label: m.nome,
                  value: String(m.id),
                }))}
                selectedValue={medIdSelecionado}
                onSelect={setMedIdSelecionado}
              />

              <FormInput
                titulo="Dosagem"
                value={dosagem}
                onChangeText={setDosagem}
                placeHolder="Ex: 1"
                keyboardType="numeric"
              />
              <FormInput
                titulo="Instruções"
                value={instrucoes}
                onChangeText={setInstrucoes}
                placeHolder="Ex: Com água"
              />
            </>
          )}

          {etapa === 2 && (
            <>
              <Text style={styles.tittle}>Data Início</Text>
              <FormDatePicker value={dataInicio} onChange={setDataInicio} />
              <Text style={styles.tittle}>Data Término</Text>
              <FormDatePicker value={dataTermino} onChange={setDataTermino} />
            </>
          )}

          {etapa === 3 && (
            <>
              <DropdownInput
                label="Frequência"
                placeholder="Selecione a Frequência..."
                items={Object.keys(TipoDeFrequencia)
                  .filter((k) => isNaN(Number(k)))
                  .map((k) => ({
                    label: k.replace(/_/g, " "),
                    value: String(
                      TipoDeFrequencia[k as keyof typeof TipoDeFrequencia]
                    ),
                  }))}
                selectedValue={freqTipo}
                onSelect={setFreqTipo}
              />

              {Number(freqTipo) === TipoDeFrequencia.INTERVALO_HORAS && (
                <FormInput
                  titulo="Intervalo (Horas)"
                  value={intervalo}
                  onChangeText={setIntervalo}
                  keyboardType="numeric"
                  placeHolder="Ex: 8"
                />
              )}
              {Number(freqTipo) === TipoDeFrequencia.HORARIOS_ESPECIFICOS && (
                <FormInput
                  titulo="Horários"
                  value={horarios}
                  onChangeText={setHorarios}
                  placeHolder="Ex: 08:00, 20:00"
                />
              )}

              <DropdownInput
                label="Alerta"
                placeholder="Selecione o tipo de Alerta..."
                items={Object.keys(TipoDeAlerta)
                  .filter((k) => isNaN(Number(k)))
                  .map((k) => ({
                    label: k.replace(/_/g, " "),
                    value: String(TipoDeAlerta[k as keyof typeof TipoDeAlerta]),
                  }))}
                selectedValue={alertaTipo}
                onSelect={setAlertaTipo}
              />
            </>
          )}
        </ScrollView>
      </View>

      <View style={{ paddingTop: 10 }}>
        {etapa < 3 ? (
          <>
            <ActionButton titulo="PROSSEGUIR" onPress={validarEAvancar} />
            <ActionButton
              titulo={etapa === 1 ? "CANCELAR" : "VOLTAR"}
              onPress={() => (etapa === 1 ? onClose() : setEtapa(etapa - 1))}
            />
          </>
        ) : (
          <>
            {isLoading ? (
              <ActivityIndicator size="large" color="#0000ff" />
            ) : (
              <ActionButton titulo="SALVAR" onPress={handleSalvar} />
            )}
            <ActionButton
              titulo="VOLTAR"
              onPress={() => setEtapa(2)}
              disabled={isLoading}
            />
          </>
        )}
      </View>
    </KeyboardAwareScrollView>
  );
}
