Hi Viesure team! I have been working on the interview task and tried to implement it in a best way possible given the time constraint. Thank you very much for going through it.

I have two Test classes:


- getWeatherTest, which is a core API task
I am sending updateTemperature PUT request, to update the temperature and use GET request to rertieve updated information.
First of all, I make sure that PUT request was successful and updated my value. Afterthat, I check values by instructions given in your QA project.
I have used DataProvider to parametrize inputs. This concluded me in following results:

<img width="467" alt="image" src="https://github.com/simkinam/apiViesure/assets/152646712/1fee0b5f-2b0d-47f6-8ce7-feb9cad0afa5">
As you can see, some of the tests have failed. 
This was an actual problem:
<img width="561" alt="image" src="https://github.com/simkinam/apiViesure/assets/152646712/f167b89a-b96c-407a-9e48-7534472bd142">
I used a classic simple rounding first (Math one, so that 0.5 rounds to the nearest next, and then I tried also with the simple Integer cut).
Its either I did not get what kind of rounding is expected, or is an actual Bug - dependent on actual expectation.
I have checked with a value given in instruction - that works. The rest of requirements seem to be met, too.
As well, I noticed that API does not have restrictions for integer values, and, therefore, they can be very unrealistic, which does not end up into an error, 
and is someting I would also raise to a developer.
I used Step Annotation for allure reporting. I am aware you use a different approach and I am currently doing an online course for Cucumber :)


- OpenWeatherMapTest, which tests the Webpage using selenium Selenium Webdriver
  As I work with Selenium UI more often, than with API, this one was a bit more usual to me. I have created a couple tests and helping functions, not to write repetitive code and avoid confusion if someone new reads my code.
Each function is assigned to a Step annotation, too.
I used a few By objects, also to avoid repetitve code.
First test navigate to a webpage, check the placeholder and verify that Sydney was chosen. The second one is negative. And third one looks up for a notification.
This is a basic scenario.
In a real world, I would also use more of PageObject model when navigating from one page to another and have clear expectation, when a page was open (such as visibility of elements and other conditions within constructor).
Also the Webdriver wait would be a global variable instead of seen inside the test. 

The UI tests were successful. 
