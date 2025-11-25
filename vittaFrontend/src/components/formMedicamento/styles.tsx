import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
  modalContent: {
    zIndex: 1,
  },
  formBody: {
    width: 350,
    backgroundColor: '#1CBDCF',
    borderRadius: 20,
    margin: 16,
  },
  formRequestBody: {
    gap: 1,
    padding: 20,
  },
  errorContainer: {
    width: '100%',
    backgroundColor: '#FFDEDE',
    padding: 10,
    borderRadius: 8,
    marginBottom: 16,
    borderWidth: 1,
    borderColor: '#D8000C',
  },
  errorText: {
    color: '#D8000C',
    fontSize: 14,
    fontWeight: '500',
    textAlign: 'center',
  },
  tittle: {
    color: '#000',
    fontWeight: "bold",
    fontSize: 18,
    marginBottom: 6,
  },
  pickerContainer: {
    backgroundColor: '#F0F0F0',
    borderRadius: 8,
    borderWidth: 1,
    borderColor: '#E0E0E0',
    height: 60,
    justifyContent: "center",
    overflow: "hidden",
  },
  picker: {
    width: "100%",
    height: "100%",
    backgroundColor: "transparent",
    color: '#000',
  },
})