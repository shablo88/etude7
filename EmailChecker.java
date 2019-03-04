import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class EmailChecker {
    /* main method
     */
    public static void main(String[] args) {
        // creates a scanner object to read inputs
        Scanner scan = new Scanner(System.in);
        List<String> inputs = new ArrayList<>();

        while(scan.hasNextLine()){
            emailCheck(scan.nextLine());
        }
        scan.close();
    }


    public static void emailCheck(String input) {
        input = input.toLowerCase();

        boolean altered = false;

        //if the input contains '@', do nothing, if it contains "_at_" then replace the last one
        if(input.contains("_at_") && !input.contains("@")) {
            StringBuilder b = new StringBuilder(input);
            b.replace(input.lastIndexOf("_at_"), input.lastIndexOf("_at_") + 4, "@" );
            input = b.toString();
        }

        //checks the index of the @ symbol to split the email into two parts
        int indexAt = input.indexOf('@');
        int indexDot = input.lastIndexOf("_dot_");

        //replace the only last instance of _dot_ to ensure validation
        if(input.contains("_dot_") && input.lastIndexOf("_dot_") > indexAt) {
            StringBuilder b = new StringBuilder(input);
            b.replace(input.lastIndexOf("_dot_"), input.lastIndexOf("_dot_") + 5, "." );
            input = b.toString();
        }

        String output = input;

        //ensures the @ symbol is present
        if (indexAt <= 0) {
            output += " <- Missing @ symbol";
            altered = true;
        }

        //else, ensures the mailbox name contains valid characters
        else {
            String mailbox = input.substring(0, indexAt);

            Pattern pattern = Pattern.compile("[A-Za-z0-9+_.-]+");
            Matcher mat = pattern.matcher(mailbox);

            boolean charViolation = false;

            //ensures that special characters do not repeat in sequence
            if(Stream.of("--", "__", "..", "-.", "-_", "_.", "_-", ".-", "._").anyMatch(mailbox::contains)) {
                charViolation = true;
            }

            //ensures the mailbox does not begin with a special character
            if(mailbox.charAt(0) == '-' || mailbox.charAt(0) == '.' || mailbox.charAt(0) == '_') {
                charViolation = true;
            }

            //ensures the mailbox does not end with a special character
            if(mailbox.charAt(mailbox.length()-1) == '-' || mailbox.charAt(mailbox.length()-1) == '.'
                    || mailbox.charAt(mailbox.length()-1) == '_') {
                charViolation = true;
            }

            if (!mat.matches() || charViolation) {
                output += " <- Invalid mailbox name";
                altered = true;
            }
        }

        if (indexAt < 0) {
            indexAt = 0;
        }

        //takes the remaining part of the email, i.e. the domain
        String domain;
        domain = input.substring(indexAt+1);
        indexDot = indexDot-indexAt-1;

        //checks to see if the domain is listed as an address
        if (domain.charAt(0) == '[' && domain.charAt(domain.length() - 1) == ']') {
            Pattern address = Pattern.compile("\\[+[0-9.]+]");
            //checks if the address consists of only numeric or dot characters
            Matcher addressMat = address.matcher(domain);

            int start = 1;
            int end = domain.indexOf('.');

            //ensures each part of the address is between 0 and 255 to be valid
            for(int i = 0; i < 4; i++) {
                String stringIP = domain.substring(start,end);
                int intIP = Integer.parseInt(stringIP);
                if(intIP >= 256 || intIP < 0) {
                    if (!altered) {
                        output += " <- Invalid numerical address";
                        altered = true;
                    } else {
                        output += ", invalid numerical address";
                    }
                    break;
                }
                start = end+1;
                end = domain.indexOf('.', start+1);
                if(end < 0) {
                    end = domain.length()-1;
                }
            }

            //if the numerical address doesn't match the format
            if (!addressMat.matches()) {
                if (!altered) {
                    output += " <- Invalid address";
                    altered = true;
                } else {
                    output += ", invalid address";
                }
            }
        }
        //else, the domain is given as text
        else {
            int domainExt = 0;
            boolean domainAdd = false;

            //set up an ArrayList of valid domains to check against
            ArrayList<String> validDomains = new ArrayList<>();
            validDomains.add(".co.nz");
            validDomains.add(".com.au");
            validDomains.add(".com.ca");
            validDomains.add(".co.us");
            validDomains.add(".co.uk");
            //.com is added last to prevent triggering "invalid domain extension" in the case of .com.ca or .com.au
            validDomains.add(".com");

            //for each valid domain
            for (String validDomain : validDomains) {
                //only do the following if no previous domain extension has been validated
                if(!domainAdd) {
                    //if the valid domain extension exists, check that it is the last part of the domain
                    if (domain.lastIndexOf(validDomain) > 0 && domain.lastIndexOf(validDomain) == domain.length() - validDomain.length()) {
                        domainExt = domain.lastIndexOf(validDomain);
                        //ensure that the "_dot_" replaced earlier is in the right place
                        if (indexDot > 0 && indexDot != domainExt) {
                            if (!altered) {
                                output += " <- Invalid domain extension";
                                altered = true;
                            } else {
                                output += ", invalid domain extension";
                            }
                        }
                        domainAdd = true;
                    }
                }
            }


            if(domainAdd == false) {
                if (!altered) {
                    output += " <- Invalid extension";
                    altered = true;
                } else {
                    output += ", invalid extension";
                }
            }

            if (domainAdd) {
                String domainName = domain.substring(0, domainExt);

                Pattern pattern = Pattern.compile("[A-Za-z0-9.]+");
                Matcher mat = pattern.matcher(domainName);

                boolean charViolation = false;

                //ensures that special characters do not repeat in sequence
                if(domainName.contains("..")) {
                    charViolation = true;
                }

                //ensures the domain name does not begin with a special character
                if(domainName.charAt(0) == '.') {
                    charViolation = true;
                }

                //ensures the domain name does not end with a special character
                if(domainName.charAt(domainName.length()-1) == '.') {
                    charViolation = true;
                }

                if (!mat.matches() || charViolation) {
                    if(!altered) {
                        output += " <- Invalid domain name";
                        altered = true;
                    } else {
                        output += ", invalid domain name";
                    }
                }
            }
        }

        if(altered) {
            System.out.println(output);
        } else {
            System.out.println(input);
        }
    }
}



