// GpService.aidl.aidl
package com.gprinter.aidl;

// Declare any non-default types here with import statements

interface GpService {
   int openPort(int PrinterId, int PortType,String DeviceName, int PortNumber);
   void closePort(int PrinterId);
   int getPrinterConnectStatus(int PrinterId);
   int printTestPage(int PrinterId);
   void queryPrinterStatus(int PrinterId, int Timesout, int requestCode);
   int getPrinterCommandType(int PrinterId);
   int sendEscCommand(int PrinterId, String b64);
   int sendLabelCommand(int PrinterId, String b64);
   void isUserExperence(boolean userExperience);
   String getClientID();
   int setServerIP(String ip, int port);
}
