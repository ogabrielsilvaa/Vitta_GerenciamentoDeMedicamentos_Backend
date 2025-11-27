import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
  modalContent: {
    zIndex: 1,
  },
  container: {
    gap: 1,
  },
  screenContent: {
    gap: 1,
    justifyContent: "center",
    alignItems: "center",
    marginBottom: 180,
    zIndex: 1,
  },
  formBox: {
    width: 350,
    backgroundColor: '#1CBDCF',
    borderRadius: 20,
    margin: 16,
    elevation: 5,
    shadowColor: '#000',
    shadowOffset: {
      width: 0,
      height: 2,
    },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,
  },
  formBody: {
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
});
