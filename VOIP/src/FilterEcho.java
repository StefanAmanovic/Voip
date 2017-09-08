/**
 * Created by SilentStorm1 on 29.7.2017..
 */
public class FilterEcho
    {
        private byte[] m_ayEchoFreeSignal;// e
        private byte[] m_ayEchoSignal;// d
        private byte[] m_ayTransposeOfSpeakerSignal;// X'
        private double[] m_adWeights;// W

        /**
         * The transpose and the weights need to be updated before applying the filter
         * to an echo signal again.
         *
         * @param ayEchoSignal
         * @param ayTransposeOfSpeakerSignal
         * @param adWeights
         */
        public FilterEcho(byte[] ayEchoSignal, byte[] ayTransposeOfSpeakerSignal, double[] adWeights)
        {
            m_ayEchoSignal = ayEchoSignal;
            m_ayTransposeOfSpeakerSignal = ayTransposeOfSpeakerSignal;
            m_adWeights = adWeights;
        }


        public byte[] applyFilter(byte[] ayAudioBytes)
        {
            // e = d - X' * W
            m_ayEchoFreeSignal = new byte[ayAudioBytes.length];
            for (int i = 0; i < m_ayEchoFreeSignal.length; ++i)
            {
                m_ayEchoFreeSignal[i] = (byte) (m_ayEchoSignal[i] - m_ayTransposeOfSpeakerSignal[i] * m_adWeights[i]);
            }
            return m_ayEchoFreeSignal;
        }}

