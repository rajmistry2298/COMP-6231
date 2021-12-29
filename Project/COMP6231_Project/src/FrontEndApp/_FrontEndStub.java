package FrontEndApp;


/**
* FrontEndApp/_FrontEndStub.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from FrontEnd.idl
* Sunday, 2 August, 2020 4:18:13 PM EDT
*/

public class _FrontEndStub extends org.omg.CORBA.portable.ObjectImpl implements FrontEndApp.FrontEnd
{

  public String createPlayerAccount (String firstName, String lastName, String age, String username, String password, String ipAddress)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("createPlayerAccount", true);
                $out.write_string (firstName);
                $out.write_string (lastName);
                $out.write_string (age);
                $out.write_string (username);
                $out.write_string (password);
                $out.write_string (ipAddress);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return createPlayerAccount (firstName, lastName, age, username, password, ipAddress        );
            } finally {
                _releaseReply ($in);
            }
  } // createPlayerAccount

  public String playerSignIn (String username, String password, String ipAddress)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("playerSignIn", true);
                $out.write_string (username);
                $out.write_string (password);
                $out.write_string (ipAddress);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return playerSignIn (username, password, ipAddress        );
            } finally {
                _releaseReply ($in);
            }
  } // playerSignIn

  public String playerSignOut (String username, String ipAddress)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("playerSignOut", true);
                $out.write_string (username);
                $out.write_string (ipAddress);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return playerSignOut (username, ipAddress        );
            } finally {
                _releaseReply ($in);
            }
  } // playerSignOut

  public String transferAccount (String username, String password, String oldIpAddress, String newIpAddress)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("transferAccount", true);
                $out.write_string (username);
                $out.write_string (password);
                $out.write_string (oldIpAddress);
                $out.write_string (newIpAddress);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return transferAccount (username, password, oldIpAddress, newIpAddress        );
            } finally {
                _releaseReply ($in);
            }
  } // transferAccount

  public String getPlayerStatus (String ipAddress)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("getPlayerStatus", true);
                $out.write_string (ipAddress);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return getPlayerStatus (ipAddress        );
            } finally {
                _releaseReply ($in);
            }
  } // getPlayerStatus

  public String suspendAccount (String usernameToSuspend, String ipAddress)
  {
            org.omg.CORBA.portable.InputStream $in = null;
            try {
                org.omg.CORBA.portable.OutputStream $out = _request ("suspendAccount", true);
                $out.write_string (usernameToSuspend);
                $out.write_string (ipAddress);
                $in = _invoke ($out);
                String $result = $in.read_string ();
                return $result;
            } catch (org.omg.CORBA.portable.ApplicationException $ex) {
                $in = $ex.getInputStream ();
                String _id = $ex.getId ();
                throw new org.omg.CORBA.MARSHAL (_id);
            } catch (org.omg.CORBA.portable.RemarshalException $rm) {
                return suspendAccount (usernameToSuspend, ipAddress        );
            } finally {
                _releaseReply ($in);
            }
  } // suspendAccount

  // Type-specific CORBA::Object operations
  private static String[] __ids = {
    "IDL:FrontEndApp/FrontEnd:1.0"};

  public String[] _ids ()
  {
    return (String[])__ids.clone ();
  }

  private void readObject (java.io.ObjectInputStream s) throws java.io.IOException
  {
     String str = s.readUTF ();
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     org.omg.CORBA.Object obj = orb.string_to_object (str);
     org.omg.CORBA.portable.Delegate delegate = ((org.omg.CORBA.portable.ObjectImpl) obj)._get_delegate ();
     _set_delegate (delegate);
   } finally {
     orb.destroy() ;
   }
  }

  private void writeObject (java.io.ObjectOutputStream s) throws java.io.IOException
  {
     String[] args = null;
     java.util.Properties props = null;
     org.omg.CORBA.ORB orb = org.omg.CORBA.ORB.init (args, props);
   try {
     String str = orb.object_to_string (this);
     s.writeUTF (str);
   } finally {
     orb.destroy() ;
   }
  }
} // class _FrontEndStub
