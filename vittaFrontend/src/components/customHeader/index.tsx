import { MaterialCommunityIcons } from "@expo/vector-icons";
import { router } from "expo-router";
import React from "react";
import { Pressable, Text, View } from "react-native";
import { useSafeAreaInsets } from "react-native-safe-area-context";
import { styles } from "./styles";

export function CustomHeader() {
  const insets = useSafeAreaInsets();

  return (
    <View style={[styles.headerContainer, { paddingTop: insets.top + 10 }]}>
      <Pressable onPress={() => router.push("/perfil")}> 
        <MaterialCommunityIcons
          name="account-circle"
          size={40}
          color="#1CBDCF"
        />
      </Pressable>

      <Text style={styles.logoHeader}>Vitta</Text>
    </View>
  );
}
