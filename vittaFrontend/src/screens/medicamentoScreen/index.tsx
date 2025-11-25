import {
  listarMedicamentos,
  medicamentoDeletar,
} from "@/src/api/medicamentoApi";
import { ActionButton } from "@/src/components/actionButton/actionButton";
import { AlertModal } from "@/src/components/alertModal";
import { DeletePressable } from "@/src/components/deletePressable";
import FormularioMedicamento from "@/src/components/formMedicamento";
import { useSession } from "@/src/contexts/authContext";
import { MedicamentoResponse } from "@/src/types/medicamentoTypes";
import { MaterialCommunityIcons } from "@expo/vector-icons";
import { useCallback, useEffect, useState } from "react";
import {
  ActivityIndicator,
  FlatList,
  Modal,
  Pressable,
  Text,
  View,
} from "react-native";
import {
  SafeAreaView,
  useSafeAreaInsets,
} from "react-native-safe-area-context";
import { styles } from "./styles";

const MAPA_TIPO_MEDICAMENTO: Record<string, string> = {
  MG: "Miligramas",
  G: "Gramas",
  ML: "Mililitros",
  GOTAS: "Gotas",
  COMPRIMIDOS: "Comprimidos",
  CAPSULAS: "Cápsulas",
};

const formatarEnum = (
  valor: string | undefined,
  mapa: Record<string, string>
) => {
  if (!valor) return "Não definido";
  return mapa[valor] || valor;
};

export default function MedicamentoScreen() {
  const { session } = useSession();
  const [medicamentos, setMedicamentos] = useState<MedicamentoResponse[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  const [modalVisivel, setModalVisivel] = useState(false);

  const [alertVisivel, setAlertVisivel] = useState(false);
  const [itemParaDeletar, setItemParaDeletar] = useState<number | null>(null);

  const [isDeleting, setIsDeleting] = useState(false);

  const [itemEmEdicao, setItemEmEdicao] = useState<MedicamentoResponse | null>(
    null
  );

  const [modalVerVisivel, setModalVerVisivel] = useState(false);
  const [itemParaVer, setItemParaVer] = useState<MedicamentoResponse | null>(
    null
  );

  const insets = useSafeAreaInsets();

  const carregarMedicamentosCadastrados = useCallback(async () => {
    if (!session) return;

    setLoading(true);
    setError(null);

    try {
      const data = await listarMedicamentos(session);
      setMedicamentos(data);
    } catch (error) {
      setError("Falha ao carregar medicamentos.");
      console.error(error);
    } finally {
      setLoading(false);
    }
  }, [session]);

  useEffect(() => {
    carregarMedicamentosCadastrados();
  }, [carregarMedicamentosCadastrados]);

  const executarDelete = async (medicamentoId: number) => {
    if (!session) {
      setError("Sessão expirada. Faça login novamente.");
      return;
    }

    setIsDeleting(true);
    setError(null);

    try {
      await medicamentoDeletar(session, medicamentoId);

      setMedicamentos((medicamentosAnteriores) =>
        medicamentosAnteriores.filter((med) => med.id !== medicamentoId)
      );
    } catch (error) {
      console.error("Erro ao deletar medicamento: ", error);
      setError("Falha ao deletar o medicamento. Tente novamente.");
    } finally {
      setIsDeleting(false);
    }
  };

  const handleDeletarMedicamento = (medicamentoId: number) => {
    console.log("FUNÇÃO handleDeletarMedicamento FOI CHAMADA");
    if (isDeleting) {
      console.log("Retornando porque isDeleting é true");
      return;
    }

    setItemParaDeletar(medicamentoId);
    setAlertVisivel(true);
  };

  const onConfirmarDelete = () => {
    // se não tiver um item selecionado, não faz nada
    if (itemParaDeletar === null) {
      setAlertVisivel(false);
      return;
    }

    // chama função de deletar que já funciona
    executarDelete(itemParaDeletar);

    // limpa o estado
    setAlertVisivel(false);
    setItemParaDeletar(null);
  };

  // função para o botão "Cancelar" do AlertModal
  const onCancelarDelete = () => {
    setAlertVisivel(false);
    setItemParaDeletar(null);
  };

  const abrirModalCadastro = () => {
    setItemEmEdicao(null);
    setModalVisivel(true);
  };

  const abrirModalEdicao = (medicamento: MedicamentoResponse) => {
    setItemEmEdicao(medicamento);
    setModalVisivel(true);
  };

  const abrirModalVer = (medicamento: MedicamentoResponse) => {
    setItemParaVer(medicamento);
    setModalVerVisivel(true);
  };

  const fecharModalVer = () => {
    setModalVerVisivel(false);
    setItemParaVer(null);
  };

  if (loading) {
    return (
      <View style={styles.container}>
        <ActivityIndicator size="large" color="#0000ff" />
        <Text>Carregando...</Text>
      </View>
    );
  }

  if (error) {
    return (
      <View style={styles.container}>
        <Text style={{ color: "red" }}>{error}</Text>
      </View>
    );
  }

  return (
    <SafeAreaView style={styles.container}>
      <Modal
        visible={modalVisivel}
        animationType="slide"
        transparent={true}
        onRequestClose={() => setModalVisivel(false)}
      >
        <View style={styles.modalContainer}>
          {modalVisivel && (
            <FormularioMedicamento
              onClose={() => setModalVisivel(false)}
              onSaveSuccess={carregarMedicamentosCadastrados}
              medicamentoParaEditar={itemEmEdicao}
            />
          )}
        </View>
      </Modal>

      <Text style={styles.title}>SEUS MEDICAMENTOS</Text>

      <FlatList
        style={styles.flatList}
        contentContainerStyle={{ alignItems: "center" }}
        data={medicamentos}
        keyExtractor={(item) => item.id.toString()}
        ListEmptyComponent={
          <View style={styles.emptyContainer}>
            <MaterialCommunityIcons name="pill" size={64} color='#d0d0d0' />
            <Text style={styles.emptyText}>
              Nenhum medicamento cadastrado no estoque.
            </Text>
          </View>
        }
        renderItem={({ item }) => (
          <View style={styles.cardsContainer}>
            <View style={styles.cardContent}>
              <View style={styles.cardTopRow}>
                <View>
                  <MaterialCommunityIcons name="pill" size={36} color="white" />
                </View>

                <View>
                  <Text style={[styles.textContent, { fontSize: 16 }]}>
                    Medicamento: {item.nome}
                  </Text>

                  <Text style={[styles.textContent, { fontSize: 16 }]}>
                    Laboratório: {item.laboratorio}
                  </Text>

                  <Text style={[styles.textContent, { fontSize: 16 }]}>
                    Tipo: {item.tipoUnidadeDeMedida}
                  </Text>
                </View>
              </View>

              <View style={styles.cardBottomRow}>
                <Pressable
                  style={styles.pressableButton}
                  onPress={() => {
                    abrirModalVer(item);
                  }}
                  disabled={isDeleting}
                >
                  <MaterialCommunityIcons name="eye" size={24} color="black" />
                </Pressable>

                <View style={styles.rightButtonsContainer}>
                  <Pressable
                    style={styles.pressableButton}
                    onPress={() => {
                      console.log("DADO DA API:", item.tipoUnidadeDeMedida);
                      console.log(
                        "TIPO DO DADO:",
                        typeof item.tipoUnidadeDeMedida
                      );
                      abrirModalEdicao(item);
                    }}
                    disabled={isDeleting}
                  >
                    <MaterialCommunityIcons
                      name="pencil"
                      size={24}
                      color="black"
                    />
                  </Pressable>

                  <DeletePressable
                    onPress={() => handleDeletarMedicamento(item.id)}
                    disabled={isDeleting}
                  />
                </View>
              </View>
            </View>
          </View>
        )}
      />
      <Modal
        visible={modalVerVisivel}
        animationType="slide"
        transparent={true}
        onRequestClose={() => setModalVisivel(false)}
      >
        <View style={styles.viewModalContainer}>
          <View style={styles.viewModalContent}>
            <Text style={styles.viewModalTitle}>{itemParaVer?.nome}</Text>

            <Text style={styles.viewModalSectionTitle}>Medicamento</Text>
            <Text style={styles.viewModalText}>Nome: {itemParaVer?.nome}</Text>

            <Text style={styles.viewModalText}>
              Laboratório: {itemParaVer?.laboratorio}
            </Text>

            <Text style={styles.viewModalText}>
              Principio Ativo: {itemParaVer?.principioAtivo}
            </Text>

            <Text style={styles.viewModalText}>
              Tipo do medicamento:{" "}
              {formatarEnum(
                String(itemParaVer?.tipoUnidadeDeMedida),
                MAPA_TIPO_MEDICAMENTO
              )}
            </Text>

            <ActionButton
              style={{ marginTop: 20 }}
              titulo="FECHAR"
              onPress={fecharModalVer}
            />
          </View>
        </View>
      </Modal>

      <AlertModal
        visible={alertVisivel}
        title="CONFIRMAR EXCLUSÃO"
        message="Tem certeza que deseja excluir este medicamento ?"
        onClose={onCancelarDelete}
        onConfirm={onConfirmarDelete}
        confirmText="Excluir"
        isDestructive={true}
      />

      <Pressable
        style={({ pressed }) => [styles.fab, pressed && styles.fabPressed]}
        onPress={abrirModalCadastro}
      >
        <MaterialCommunityIcons name="plus" size={30} color="#1CBDCF" />
      </Pressable>
    </SafeAreaView>
  );
}
