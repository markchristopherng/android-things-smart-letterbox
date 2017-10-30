var functions = require('firebase-functions');


var admin = require('firebase-admin');
var mainApp = admin.initializeApp(functions.config().firebase);

var smtpTransport = require('nodemailer-smtp-transport');
var nodemailer = require('nodemailer');
var options = {};
var transporter = nodemailer.createTransport(
      smtpTransport({
        service: 'gmail',
        host: 'smtp.gmail.com',
        auth: {
          user: 'your_email',
          pass: 'your_password'
        }
      }));
const THRESHOLD = 5;

exports.sendemail = functions.database.ref('postboxes/0/lettercount').onUpdate(function(childSnapshot){
                //read the letter count value
                var data = childSnapshot.data.val();
                if (data > THRESHOLD) {
                  // setup email data with unicode symbols
                  let mailOptions = {
                      from: 'your_email', // sender address
                      to: 'other_email', // list of receivers
                      subject: 'You have received a new letter!', // Subject line
                      text: 'Letter count : ' + data, // plain text body
                  };
                  // send mail with defined transport object
                  transporter.sendMail(mailOptions, (error, info) => {
                      if (error) {
                          return console.log(error);
                      }
                  });
                }
            });
