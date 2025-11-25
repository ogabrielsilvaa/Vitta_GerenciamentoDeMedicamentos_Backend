import { StyleSheet } from "react-native";

export const styles = StyleSheet.create({
  containerExternoCalendario: {
    borderRadius: 10,
    marginTop: 10,
    marginHorizontal: 10,
    marginBottom: 30,
    elevation: 5,
    shadowColor: '#000',
    shadowOffset: { width: 0, height: 2 },
    shadowOpacity: 0.25,
    shadowRadius: 3.84,
    backgroundColor: 'white',
  },
  containerInternoCalendario: {
    width: 340,
    height: 360,
    marginHorizontal:10,
    marginBottom: 30,
    borderRadius: 10,
    overflow: 'hidden',
  },
})