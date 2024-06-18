/*import { EmailTemplate } from '@/components/resend/email-template';
import { Resend } from 'resend';
import { NextResponse } from 'next/server';
import { NextApiRequest, NextApiResponse } from 'next';


const resend = new Resend('re_CNirM2Qu_AcdpoGvv7c8KjJUXVGPbGS6Z');
export default async function handler(req: NextApiRequest, res: NextApiResponse) {
  if (req.method !== 'POST') {
    res.setHeader('Allow', ['POST']);
    return res.status(405).end('Method Not Allowed');
  }

  try {
    const { senderEmail, receiverEmail } = req.body; // Asegúrate de recibir los datos correctos
    const senderResponse = await resend.emails.send({
      from: 'onboarding@resend.dev',
      to: 'a20172360@pucp.edu.pe',
      subject: 'Confirmación de Envío',
      react: EmailTemplate({ firstName: 'Emisor' }),
      text: ""
    });

    const receiverResponse = await resend.emails.send({
      from: 'onboarding@resend.dev',
      to: 'a20172360@pucp.edu.pe',
      subject: 'Notificación de Recepción',
      react: EmailTemplate({ firstName: 'Receptor' }),
      text: ""
    });

    res.status(200).json({
      message: 'Emails sent successfully',
      senderResponse,
      receiverResponse
    });
  } catch (error : any) {
    console.error('Error sending email:', error);
    res.status(500).json({ error: 'Failed to send email', details: error.toString() });
  }
}*/