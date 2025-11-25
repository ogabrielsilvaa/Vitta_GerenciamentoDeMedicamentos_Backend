import { MaterialCommunityIcons } from "@expo/vector-icons";
import React, { useState } from "react";
import { ScrollView, Text, TouchableOpacity, View } from "react-native";
import { styles } from "./styles";

type DropdownItem = {
  label: string;
  value: string;
};

type DropdownInputProps = {
  label?: string;
  placeholder?: string;
  items: DropdownItem[];
  selectedValue: string;
  onSelect: (value: string) => void;
};

export function DropdownInput({
  label,
  items,
  selectedValue,
  onSelect,
  placeholder = "Selecione...",
}: DropdownInputProps) {
  const [isOpen, setIsOpen] = useState(false);

  const selectedItem = items.find((i) => i.value === selectedValue);
  const displayText = selectedItem ? selectedItem.label : placeholder;

  const handleSelect = (value: string) => {
    onSelect(value);
    setIsOpen(false);
  };

  return (
    <View style={styles.container}>
      {label && <Text style={styles.label}>{label}</Text>}

      <TouchableOpacity
        style={styles.dropdownButton}
        onPress={() => setIsOpen(!isOpen)}
        activeOpacity={0.7}
      >
        <Text
          style={
            selectedItem
              ? styles.dropdownButtonText
              : styles.dropdownPlaceholder
          }
        >
          {displayText}
        </Text>
        <MaterialCommunityIcons
          name={isOpen ? "chevron-up" : "chevron-down"}
          size={24}
          color="#666"
        />
      </TouchableOpacity>

      {isOpen && (
        <View style={styles.dropdownList}>
          <ScrollView nestedScrollEnabled={true} style={{ maxHeight: 200 }}>
            {items.length === 0 ? (
              <Text style={{ padding: 15, color: "#999", textAlign: "center" }}>
                Nenhuma opção disponível
              </Text>
            ) : (
              items.map((item) => {
                const isSelected = item.value === selectedValue;
                return (
                  <TouchableOpacity
                    key={item.value}
                    style={styles.dropdownItem}
                    onPress={() => handleSelect(item.value)}
                  >
                    <Text
                      style={[
                        styles.dropdownItemText,
                        isSelected && styles.dropdownItemTextSelected,
                      ]}
                    >
                      {item.label}
                    </Text>
                    {isSelected && (
                      <MaterialCommunityIcons
                        name="check"
                        size={18}
                        color="#1CBDCF"
                      />
                    )}
                  </TouchableOpacity>
                );
              })
            )}
          </ScrollView>
        </View>
      )}
    </View>
  );
}
