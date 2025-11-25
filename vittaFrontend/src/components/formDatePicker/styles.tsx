import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
  container: {
    marginBottom: 10,
  },
  pickerContainer: {
    flexDirection: 'row',
    justifyContent: 'space-between', // Espalha texto e ícone
    alignItems: 'center',
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 8,
    backgroundColor: '#fff',
    height: 50,
    marginBottom: 10,
  },
  picker: {
    width: "85%", // Deixa espaço para o ícone
  },
  pickerText: {
    fontSize: 16,
    color: "#000",
    paddingHorizontal: 15,
  },
});