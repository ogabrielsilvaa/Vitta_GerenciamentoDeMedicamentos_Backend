import { AgendamentoResponse } from '@/src/types/agendamentoTypes';
import { format } from 'date-fns';
import React, { useMemo } from 'react';
import { View } from 'react-native';
import { Calendar, LocaleConfig } from 'react-native-calendars';
import { MarkedDates } from 'react-native-calendars/src/types';
import { styles } from './styles';

LocaleConfig.locales['pt-br'] = {
  monthNames: [
    'Janeiro', 'Fevereiro', 'Março', 'Abril', 'Maio', 'Junho',
    'Julho', 'Agosto', 'Setembro', 'Outubro', 'Novembro', 'Dezembro'
  ],
  monthNamesShort: ['Jan.', 'Fev.', 'Mar.', 'Abr.', 'Mai.', 'Jun.', 'Jul.', 'Ago.', 'Set.', 'Out.', 'Nov.', 'Dez.',],
  dayNames: ['Domingo', 'Segunda', 'Terça', 'Quarta', 'Quinta', 'Sexta', 'Sábado'],
  dayNamesShort: ['D', 'S', 'T', 'Q', 'Q', 'S', 'S'],
  today: "Hoje"
};
LocaleConfig.defaultLocale = 'pt-br';

interface CalendarioProps {
  agendamentos: AgendamentoResponse[];
  diaSelecionado: string | null;
  onDiaPressionado: (data: string) => void;
  onMesMudou: (data: Date) => void;
}

const Calendario: React.FC<CalendarioProps> = ({
  agendamentos,
  diaSelecionado,
  onDiaPressionado,
  onMesMudou
}) => {
  const markedDates = useMemo(() => {
    const marks: MarkedDates = {};

    agendamentos.forEach(agendamento => {
      const dateString = format(new Date(agendamento.horarioDoAgendamento), 'yyyy-MM-dd');
      marks[dateString] = {
        marked: true,
        dotColor: '#1CBDCF',
        selected: dateString === diaSelecionado
      };
    });

    const todayString = format(new Date(), 'yyyy-MM-dd');
    const todayStyles = {
      customStyles: {
        container: {
          backgroundColor: 'transparent',
          borderWidth: 1,
          borderColor: '#1CBDCF',
          borderRadius: 16,
        },
        text: {
          color: '#1CBDCF',
          fontWeight: 'bold',
        },
      },
    };
    

    if (diaSelecionado && !marks[diaSelecionado]) {
      marks[diaSelecionado] = { selected: true, selectedColor: '#1CBDCF' };
    }

    return marks;
  }, [agendamentos, diaSelecionado]);

  return (
    <View style={styles.containerExternoCalendario}>
      <View style={styles.containerInternoCalendario}>
        <Calendar
          markedDates={markedDates}
          onDayPress={(day) => {
            onDiaPressionado(day.dateString);
          }}
          onMonthChange={(month) => {
            onMesMudou(new Date(month.timestamp));
          }}

          theme={{
            arrowColor: '#1CBDCF',
            selectedDayBackgroundColor: '#1CBDCF',

            stylesheet: {
              day: {
                basic: {
                  today: {
                    container: {
                      backgroundColor: 'transparent',
                      borderWidth: 1,
                      borderColor: '#1CBDCF',
                      borderRadius: 16,
                    },

                    text: {
                      color: '#1CBDCF',
                      fontWeight: 'bold',
                    }
                  }
                }
              }
            }
          }}
        />
      </View>
    </View>
  );
}

export default Calendario;
