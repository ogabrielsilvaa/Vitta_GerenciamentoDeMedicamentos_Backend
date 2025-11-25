import { MaterialCommunityIcons } from "@expo/vector-icons";
import React, { useState } from "react";
import { Modal, Pressable, Text, TouchableOpacity, View } from "react-native";
import { Calendar, LocaleConfig } from "react-native-calendars";
import { MarkedDates } from "react-native-calendars/src/types";
import { styles } from "./styles";

LocaleConfig.locales["pt-br"] = {
  monthNames: ["Janeiro", "Fevereiro", "Março", "Abril", "Maio", "Junho", "Julho", "Agosto", "Setembro", "Outubro", "Novembro", "Dezembro"],
  monthNamesShort: ["Jan.", "Fev.", "Mar.", "Abr.", "Mai.", "Jun.", "Jul.", "Ago.", "Set.", "Out.", "Nov.", "Dez."],
  dayNames: ["Domingo", "Segunda", "Terça", "Quarta", "Quinta", "Sexta", "Sábado"],
  dayNamesShort: ["DOM", "SEG", "TER", "QUA", "QUI", "SEX", "SÁB"],
  today: "Hoje",
};
LocaleConfig.defaultLocale = "pt-br";

type FormDatePickerProps = {
  value: Date | null;
  onChange: (date: Date) => void;
};

const getSafeDateString = (date: Date | null): string | undefined => {
  if (!date) return undefined;
  if (isNaN(date.getTime())) return undefined;
  
  const year = date.getFullYear();
  const month = String(date.getMonth() + 1).padStart(2, '0');
  const day = String(date.getDate()).padStart(2, '0');
  
  return `${year}-${month}-${day}`;
};

export function FormDatePicker({ value, onChange }: FormDatePickerProps) {
  const [show, setShow] = useState(false);

  const dateString = getSafeDateString(value);

  const markedDates: MarkedDates = {};
  if (dateString) {
    markedDates[dateString] = {
      selected: true,
      selectedColor: '#1CBDCF',
      disableTouchEvent: true
    };
  }

  const handleDayPress = (day: { dateString: string }) => {
    const [ano, mes, dia] = day.dateString.split('-').map(Number);
    const dataSegura = new Date(ano, mes - 1, dia, 12, 0, 0);
    
    onChange(dataSegura);
    setShow(false);
  };

  const displayText = value ? value.toLocaleDateString("pt-BR") : "Selecione uma data...";

  return (
    <View>
      <TouchableOpacity 
        style={styles.pickerContainer} 
        onPress={() => setShow(true)}
      >
        <Text style={[styles.picker, styles.pickerText]}>
          {displayText}
        </Text>
        <MaterialCommunityIcons name="calendar" size={24} color='#666' style={{ marginRight: 10 }} />
      </TouchableOpacity>

      <Modal
        visible={show}
        transparent={true}
        animationType="fade"
        onRequestClose={() => setShow(false)}
      >
        <Pressable 
            style={{ flex: 1, backgroundColor: 'rgba(0,0,0,0.5)', justifyContent: "center", alignItems: "center" }}
            onPress={() => setShow(false)}
        >
            <Pressable 
                style={{ backgroundColor: "white", borderRadius: 10, padding: 10, width: "90%" }}
                onPress={() => {}}
            >
                <Calendar
                    current={dateString}
                    onDayPress={handleDayPress}
                    markedDates={markedDates}
                    theme={{
                        todayTextColor: '#1CBDCF',
                        arrowColor: '#1CBDCF',
                        selectedDayBackgroundColor: '#1CBDCF',
                        selectedDayTextColor: '#ffffff',
                    }}
                />
                <TouchableOpacity 
                    style={{ marginTop: 10, alignItems: "center", padding: 10 }}
                    onPress={() => setShow(false)}
                >
                    <Text style={{ color: '#1CBDCF', fontWeight: "bold" }}>CANCELAR</Text>
                </TouchableOpacity>
            </Pressable>
        </Pressable>
      </Modal>
    </View>
  );
}