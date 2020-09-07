# Spring Boot Upload/Download CSV Files with SQL database example

## Service interface
### Service port
The service exposes the https port 8443.

### Service data

The input data is in the form of CSV files, to be uploaded via a 'form-data' http request. 
There is only one avaliable input endpoint (see below). 

The output data and format depends on the service endpoint.

### Service endpoints

- /csv/upload

    Uploads a CSV file. The columns of the CSV file shall be as follows:
    
    - Supplier Id (String --- mandatory)
    - Invoice Id (String --- mandatory)
    - Invoice Date (YYYY-MM-DD, date invoice was created --- mandatory)
    - Invoice Amount (Amount without '$' --- mandatory)
    - Terms (Integer - number of days --- mandatory)
    - Payment Date (YYYY-MM-DD, may be null)
    - Payment Amount (Amount without '$', may be null)
    
    The service checks for the validity of the CSV file. 
    
    The combination of Supplier ID + Invoice ID must be unique. 
    
    If duplicates are detected in the file itself, the file is rejected, and a list of duplicate pairs returned. 
    
    Records that exist already in the database are updated with the data from the uploaded file.
    
    The response is a JSON object with the following members:
    - success (flag indicating success / failure);
    - errorType (type of error, if any);
    - errorDescription (descritpion of error, if any);
    - message (a descriptive message, present both in case of success or failure);
    - duplicateKeys (the duplicate pairs, if found in the CSV file);
    - updatedInvoices (the number of invoices updated);
    - insertedInvoices (the number of new invoices inserted);
    - invoiceCount (the total number of invoices in the database);
    
 - /csv/data
      
     This service returns a JSON array, containing all invoices in the database. 
     Every element of the array is an object containing, as members, the
     fields corresponding to the CSV columns above.                                                                
                                                                           
 - /csv/download
      
     This service returns all invoices in the database as a downloadable CSV file (MIME type 'text/csv').                                       

## Notes and todo's

The service is implemented as a SpringBoot application, using an SQL database to store the data.

Although tested with MySQL, I used H2 in the final version, so that it is easier to build a single Docker.

I alas found no way to install Docker on my PC, due to VM issues.

I therefore just "blindfoldedly" just added a "dockerize.sh" script that
creates a simple Docker.

### Optimizations

Obviously paging the results.

Also, there may be performance improvement with ad-hoc SQL statements. This is because save()/saveAll() will always download
the full record when updating.

### Test considerations

Although the "basics" are covered, it would be useful to add test verifying the
integrity of data between database and CSV files.

Also, all errors conditions should obviously be tested

