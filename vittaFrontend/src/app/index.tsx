import { ActionButton } from "@/src/components/actionButton/actionButton";
import { router } from "expo-router";
import { StyleSheet, View } from "react-native";
import Wave from "react-native-waves";
import LogoComponent from "../components/logoComponent";

export default function Index() {
  return (
    <View style={styles.container}>
      <View style={styles.contentScreen}>
        <LogoComponent />

        <View style={styles.pressablesContainer}>
          
          <ActionButton
            titulo="CADASTRAR"
            onPress={ () => router.replace('/cadastro') }
          />

          <ActionButton
            titulo="LOGIN"
            onPress={ () => router.replace('/login') }
          />
      </View>

      </View>

      <Wave
        placement="bottom"
        gap={90}
        speed={4}
        maxPoints={5}
        delta={70}
        color="#1CBDCF" />
    </View>
  );
}

export const styles = StyleSheet.create({
    container: {
        flex: 1,  
    },
    contentScreen: {
      flex: 1,
      justifyContent: "center",
      alignItems: "center",
      zIndex: 1,
    },
    textTittle: {
        fontFamily: 'inria-sans.bold',
        fontSize: 25, 
        marginTop: 18,
        marginBottom: 4, 
        color: '#000000ff',
        fontWeight: 'bold',
    },
    textSubtittle: {
        fontFamily: 'inria-sans.bold',
        fontSize: 25, 
        marginBottom: 10, 
        color: '#1CBDCF',
    },
    pressablesContainer: {
        marginTop: 8,
        marginBottom: 130,
    }
})