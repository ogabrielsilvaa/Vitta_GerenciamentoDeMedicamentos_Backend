import { medicamentoAtualizar, medicamentoCadastro } from "@/src/api/medicamentoApi";
import { useSession } from "@/src/contexts/authContext";
import { TipoUnidadeDeMedida } from "@/src/enums/medicamento/tipoUnidadeDeMedida";
import { MedicamentoCadastroData, MedicamentoResponse } from "@/src/types/medicamentoTypes";
import { Picker } from "@react-native-picker/picker";
import { useState } from "react";
import { Text, View } from "react-native";
import { KeyboardAwareScrollView } from 'react-native-keyboard-aware-scroll-view';
import { ActionButton } from "../actionButton/actionButton";
import { FormInput } from "../formInput";
import { styles } from "./styles";

const unidadesEnumKeys = Object.keys(TipoUnidadeDeMedida).filter(
  (key) => isNaN(Number(key))
);

type FormularioProps = {
  onClose: () => void;
  onSaveSuccess: () => void;
  medicamentoParaEditar?: MedicamentoResponse | null;
}

export default function FormularioMedicamento({
  onClose,
  onSaveSuccess,
  medicamentoParaEditar
}: FormularioProps) {
  const { session } = useSession();

  const [isLoading, setIsLoading] = useState(false);

  const valorEnumDaApi = medicamentoParaEditar?.tipoUnidadeDeMedida.toString();

  const valorInicialPicker = (valorEnumDaApi
    ? TipoUnidadeDeMedida[valorEnumDaApi as keyof typeof TipoUnidadeDeMedida]
    : null
  ) as TipoUnidadeDeMedida | null;

  const [nome, setNome] = useState(medicamentoParaEditar?.nome || "");
  const [principioAtivo, setPrincipioAtivo] = useState(medicamentoParaEditar?.principioAtivo || "");
  const [laboratorio, setLaboratorio] = useState(medicamentoParaEditar?.laboratorio || "");
  const [tipoUnidadeDeMedida, setTipoUnidadeDeMedida] =
    useState<TipoUnidadeDeMedida | null>(valorInicialPicker);

  const [errorMessage, setErrorMessage] = useState<string | null>(null);

  const validateForm = () => {
    if (!nome.trim()) {
      return "O campo Nome é obrigatório.";
    }
    if (!principioAtivo.trim()) {
      return "O campo Príncipio Ativo é obrigatório.";
    }
    if (tipoUnidadeDeMedida === null) {
      return "O campo Tipo Unidade de Medida é obrigatório.";
    }

    return null;
  };

  const handleSalvar = async () => {
    setErrorMessage(null);

    const validationError = validateForm();

    if (validationError) {
      setErrorMessage(validationError);
      return;
    }

    if (!session) {
      setErrorMessage("Usuário não autenticado. Faça login novamente.");
      return;
    }

    if (tipoUnidadeDeMedida === null) {
      setErrorMessage("Tipo de unidade de medida não selecionado.");
      return;
    }

    setIsLoading(true);

    const dadosDoMedicamento: MedicamentoCadastroData = {
      nome: nome.trim(),
      principioAtivo: principioAtivo.trim(),
      laboratorio: laboratorio.trim(),
      tipoUnidadeDeMedida: tipoUnidadeDeMedida,
    };

    try {
      if (medicamentoParaEditar) {
        const resposta = await medicamentoAtualizar(session, medicamentoParaEditar.id, dadosDoMedicamento);
        console.log("Medicamento atualizado com sucesso!", resposta);
      } else {
        const resposta = await medicamentoCadastro(session, dadosDoMedicamento);
        console.log("Medicamento cadastrado com sucesso!", resposta);
      }

      console.log("Formulário salvo com sucesso!");
      onSaveSuccess();
      onClose();
    } catch (error) {
      console.error("Erro na chamada de salvar: ", error);

      const verbo = medicamentoParaEditar ? "atualizar" : "cadastrar";
      setErrorMessage(`Falha ao ${verbo} o medicamento. Tente novamente.`);
    } finally {
      setIsLoading(false);
    }
  };

  const handleNomeChange = (text: string) => {
    setNome(text);
    if (errorMessage) setErrorMessage(null);
  };

  const handlePrincipioAtivoChange = (text: string) => {
    setPrincipioAtivo(text);
    if (errorMessage) setErrorMessage(null);
  };

  const handleLaboratorioChange = (text: string) => {
    setLaboratorio(text);
    if (errorMessage) setErrorMessage(null);
  };

  const handleTipoUnidadeChange = (
    itemValue: TipoUnidadeDeMedida | null
  ) => {
    setTipoUnidadeDeMedida(itemValue);
    if (errorMessage) {
      setErrorMessage(null);
    }
  };

  return (
    <KeyboardAwareScrollView
      style={{ flex: 1 }}
      resetScrollToCoords={{ x: 0, y: 0 }}
      contentContainerStyle={[styles.modalContent, { flexGrow: 1, justifyContent: "center", alignItems: "center"  }]}
      scrollEnabled={true}
      enableOnAndroid={true}
      bounces={false}
    >

      <View style={styles.formBody}>
        <View style={styles.formRequestBody}>

          {errorMessage && (
            <View style={styles.errorContainer}>
              <Text style={styles.errorText}>{errorMessage}</Text>
            </View>
          )}

          <FormInput
            titulo="Nome"
            value={nome}
            onChangeText={handleNomeChange}
            placeHolder="Digite o nome do medicamento"
            autoCapitalize="words"
          />

          <FormInput
            titulo="Principio Ativo"
            value={principioAtivo}
            onChangeText={handlePrincipioAtivoChange}
            placeHolder="Digite o príncipio ativo do medicamento"
            autoCapitalize="words"
          />

          <FormInput
            titulo="Laboratório"
            value={laboratorio}
            onChangeText={handleLaboratorioChange}
            placeHolder="Digite o laboratório do medicamento"
            autoCapitalize="words"
          />

          <Text style={styles.tittle}>Tipo Unidade de Medida</Text>

          <View style={styles.pickerContainer}>
            <Picker
              selectedValue={tipoUnidadeDeMedida}
              onValueChange={handleTipoUnidadeChange}
              style={styles.picker}
            >
              <Picker.Item label="Selecione um tipo..." value={null} />

              {unidadesEnumKeys.map((key) => (
                <Picker.Item
                  key={key}
                  label={key}
                  value={TipoUnidadeDeMedida[key as keyof typeof TipoUnidadeDeMedida]}
                />
              ))}

            </Picker>
          </View>

        </View>
      </View>

      <ActionButton
        titulo={medicamentoParaEditar ? "ATUALIZAR" : "CADASTRAR"}
        onPress={ handleSalvar }
        disabled={isLoading}
      />

      <ActionButton
        titulo="CANCELAR"
        onPress={onClose}
        disabled={isLoading}
      />

    </KeyboardAwareScrollView>
  );
}
