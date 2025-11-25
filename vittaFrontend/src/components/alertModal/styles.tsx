import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
  backdrop: {
    flex: 1,
    backgroundColor: 'rgba(0, 0, 0, 0.5)', // Fundo escurecido
    justifyContent: 'center',
    alignItems: 'center',
  },
  alertBox: {
    backgroundColor: 'white',
    borderRadius: 10,
    padding: 20,
    width: '85%', // 85% da largura da tela
    elevation: 5, // Sombra (Android)
    shadowColor: '#000', // Sombra (iOS)
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,
  },
  title: {
    fontSize: 18,
    fontWeight: 'bold',
    marginBottom: 10,
    color: '#333',
  },
  message: {
    fontSize: 16,
    color: '#555',
    marginBottom: 20,
  },
  buttonContainer: {
    flexDirection: 'row',
    justifyContent: 'flex-end', // Alinha botões à direita
  },
  button: {
    paddingVertical: 8,
    paddingHorizontal: 12,
    marginLeft: 15, // Espaço entre os botões
  },
  buttonText: {
    fontSize: 16,
    fontWeight: '500',
  },
  confirmText: {
    color: '#1CBDCF', // Sua cor primária
  },
  destructiveText: {
    color: 'red', // Vermelho para "Excluir"
  },
});