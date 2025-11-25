import { MaterialIcons } from "@expo/vector-icons";
import { KeyboardTypeOptions, Text, TextInput, View } from "react-native";
import { styles } from "./styles";

type FormInputProps = {
    titulo: string;
    value: string;
    onChangeText?: ((text: string) => void) | undefined
    placeHolder?: string;
    secureTextEntry?: true;
    keyboardType?: KeyboardTypeOptions;
    autoCapitalize?: 'none' | 'sentences' | 'words' | 'characters';
    iconName?: keyof typeof MaterialIcons.glyphMap;
};

export const FormInput = ({ 
  titulo,
  value,
  onChangeText,
  placeHolder,
  secureTextEntry,
  keyboardType,
  autoCapitalize,
  iconName
}: FormInputProps) => {
  return (
    <View style={styles.formRequestBody}>
      <Text style={styles.tittle}>{titulo}</Text>
      
      <View style={styles.inputContainer}>
        <TextInput
          style={styles.inputField}
          value={value}
          onChangeText={onChangeText}
          placeholder={placeHolder}
          secureTextEntry={secureTextEntry}
          keyboardType={keyboardType}
          autoCapitalize={autoCapitalize}
        />
        
        {iconName && (
          <MaterialIcons 
            name={iconName} 
            size={20} 
            color='#888'
            style={styles.icon} 
          />
        )}
      </View>
    </View>
  );
};
