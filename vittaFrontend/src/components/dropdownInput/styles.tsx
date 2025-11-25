import { StyleSheet } from "react-native";

export const styles = StyleSheet.create ({
  container: {
    marginBottom: 15,
  },
  label: {
    color: '#000',
    fontWeight: "bold",
    fontSize: 18,
    marginBottom: 6,
  },
  dropdownButton: {
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
    borderWidth: 1,
    borderColor: '#ccc',
    borderRadius: 8,
    backgroundColor: '#fff',
    height: 50,
    paddingHorizontal: 15,
    marginBottom: 5,
  },
  dropdownButtonText: {
    fontSize: 16,
    color: '#333',
  },
  dropdownPlaceholder: {
    fontSize: 16,
    color: '#999',
  },
  dropdownList: {
    borderWidth: 1,
    borderColor: '#eee',
    borderTopWidth: 0,
    borderRadius: 8,
    backgroundColor: '#fff',
    maxHeight: 200,
    marginBottom: 15,
    elevation: 2,
  },
  dropdownItem: {
    paddingVertical: 12,
    paddingHorizontal: 15,
    borderBottomWidth: 1,
    borderBottomColor: '#f0f0f0',
    flexDirection: "row",
    justifyContent: "space-between",
    alignItems: "center",
  },
  dropdownItemText: {
    fontSize: 16,
    color: '#333',
  },
  dropdownItemTextSelected: {
    color: '#1CBDCF',
    fontWeight: "bold",
  },
})
