import { AlertModal } from "@/src/components/alertModal";
import { AuthContext } from "@/src/contexts/authContext";
import { MaterialCommunityIcons } from "@expo/vector-icons";
import { useRouter } from "expo-router";
import { useContext, useState } from "react";
import { Pressable, Text, View } from "react-native";
import { styles } from "./styles";

export default function ProfileScreen() {
  const { signOut, usuario } = useContext(AuthContext);
  const router = useRouter();

  const [logoutModalVisible, setLogoutModalVisible] = useState(false);

  const confirmarLogout = () => {
    setLogoutModalVisible(false);
    signOut();
    router.replace('/'); 
  };

  return (
    <View style={styles.container}>
      
      <View style={styles.avatarContainer}>
        <MaterialCommunityIcons name="account" size={80} color='#1CBDCF' />
      </View>

      <Text style={styles.userName}>
        {usuario?.nome || "Usuário"}
      </Text>

      <View style={styles.infoContainer}>
        <Text style={styles.infoLabel}>E-mail</Text>
        <Text style={styles.infoValue}>{usuario?.email || "email@exemplo.com"}</Text>

        <Text style={styles.infoLabel}>Telefone</Text>
        <Text style={styles.infoValue}>{usuario?.telefone || "+12 (34) 567891001"}</Text>
      </View>

      <Pressable 
        style={({ pressed }) => [
            styles.logoutButton, 
            { opacity: pressed ? 0.6 : 1 }
        ]}
        onPress={() => setLogoutModalVisible(true)}
      >
        <MaterialCommunityIcons name="logout" size={24} color='#d32f2f' />
        <Text style={styles.logoutText}>SAIR DO APLICATIVO</Text>
      </Pressable>

      <AlertModal
        visible={logoutModalVisible}
        title="SAIR DO APP"
        message="Deseja realmente desconectar sua conta?"
        onClose={() => setLogoutModalVisible(false)}
        onConfirm={confirmarLogout}
        confirmText="Sair"
        isDestructive={true}
      />

    </View>
  );
}
