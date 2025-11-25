import { ReactNode } from "react";
import { Pressable, StyleProp, Text, ViewStyle } from "react-native";
import { styles } from "./syles";

type ActionButtonProps = {
    titulo: string;
    onPress: () => void;
    disabled?: boolean;
    icon?: ReactNode;
    style?: StyleProp<ViewStyle>;
}

export const ActionButton = ( { onPress, titulo, disabled, icon, style }: ActionButtonProps ) => {

    return (
        <Pressable
        style={[styles.pressable, style]}
        onPress={ onPress }
        disabled={ disabled }
        >
            {icon}
            <Text style={[styles.text, icon ? {marginLeft: 8}: {} ]}>{titulo}</Text>
        </Pressable>
    )
}