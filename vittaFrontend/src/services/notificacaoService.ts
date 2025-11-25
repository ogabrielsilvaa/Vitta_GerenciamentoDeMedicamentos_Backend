import * as Device from 'expo-device';
import * as Notifications from 'expo-notifications';
import { Platform } from 'react-native';

Notifications.setNotificationHandler({
  handleNotification: async () => ({
    shouldShowAlert: true,
    shouldPlaySound: true,
    shouldSetBadge: false,
    shouldShowBanner: true,
    shouldShowList: true,
  }),
});

export async function registrarNotificacoesAsync() {
  if (Platform.OS === 'android') {
    await Notifications.setNotificationChannelAsync('default', {
      name: 'default',
      importance: Notifications.AndroidImportance.MAX,
      vibrationPattern: [0, 250, 250, 250],
      lightColor: '#FF231F7C',
    });
  }

  if (!Device.isDevice) {
    console.log('Notificações físicas funcionam melhor em dispositivos reais.');
  }

  const { status: existingStatus } = await Notifications.getPermissionsAsync();
  let finalStatus = existingStatus;

  if (existingStatus !== 'granted') {
    const { status } = await Notifications.requestPermissionsAsync();
    finalStatus = status;
  }

  if (finalStatus !== 'granted') {
    return;
  }
}

export async function agendarLembreteMedicamento(titulo: string, corpo: string, dataGatilho: Date) {
  try {
    const agora = Date.now();
    const gatilho = dataGatilho.getTime();
    
    const diffSegundos = (gatilho - agora) / 1000;
    const segundosAteLa = Math.floor(diffSegundos);

    console.log(`[Notificação] Tentando agendar para: ${dataGatilho.toLocaleTimeString()}`);
    console.log(`[Notificação] Segundos restantes: ${diffSegundos}`);

    if (segundosAteLa <= 0) {
        console.log("[Notificação] Cancelada: Data já passou ou é agora.");
        return;
    }

    const id = await Notifications.scheduleNotificationAsync({
      content: {
        title: titulo,
        body: corpo,
        sound: true,
        data: { tipo: 'medicamento' },
      },
      trigger: {
        type: Notifications.SchedulableTriggerInputTypes.TIME_INTERVAL,
        seconds: segundosAteLa,
        repeats: false,
      },
    });
    
    console.log(`[Notificação] Agendada (ID: ${id}) para daqui a ${segundosAteLa}s`);
    return id;

  } catch (error) {
    console.error("[Notificação] Erro ao agendar:", error);
  }
}

export async function cancelarTodasNotificacoes() {
    await Notifications.cancelAllScheduledNotificationsAsync();
    console.log("Todas as notificações pendentes foram canceladas.");
}