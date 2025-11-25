import { MaterialCommunityIcons } from "@expo/vector-icons";
import { Pressable } from "react-native";
import { styles } from "./styles";

type DeletePressableProps = {
    onPress: () => void;
    disabled?: boolean;
}

export const DeletePressable = ( { onPress, disabled }: DeletePressableProps ) => {

  return (
          <Pressable
          style={styles.pressableButton}
          onPress={ onPress }
          disabled={ disabled }
          >
              <MaterialCommunityIcons name="trash-can" size={24} color="red" />
          </Pressable>
      )
}