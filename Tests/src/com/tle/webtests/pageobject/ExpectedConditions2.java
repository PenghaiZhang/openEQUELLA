package com.tle.webtests.pageobject;

import java.util.List;
import java.util.Set;

import org.openqa.selenium.Alert;
import org.openqa.selenium.By;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.NoSuchFrameException;
import org.openqa.selenium.SearchContext;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.internal.WrapsElement;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.tle.common.Check;
import com.tle.webtests.framework.factory.RefreshableElement;

public class ExpectedConditions2
{
	private static final By XPATH_FIRSTELEM = By.xpath("*[1]");

	public static ExpectedCondition<WebElement> updateOfElementLocated(WebElement element, final SearchContext context,
		final By locator)
	{
		final WebElement realElement;
		if( element instanceof RefreshableElement )
		{
			realElement = ((RefreshableElement) element).findNonWrapped();
		}
		else if( element instanceof WrapsElement )
		{
			realElement = ((WrapsElement) element).getWrappedElement();
		}
		else
		{
			realElement = element;
		}
		return new ExpectedCondition<WebElement>()
		{
			private boolean checkingStale = true;

			@Override
			public WebElement apply(WebDriver driver)
			{
				if( checkingStale )
				{
					try
					{
						realElement.isDisplayed();
						return null;
					}
					catch( StaleElementReferenceException se )
					{
						checkingStale = false;
					}
				}
				return elementIfVisible(context.findElement(locator));
			}

			@Override
			public String toString()
			{
				return "updateOfElementLocated " + context + ":" + locator;
			}
		};
	}

	public static ExpectedCondition<WebElement> visibilityOfElementLocated(final SearchContext context, final By locator)
	{
		return new ExpectedCondition<WebElement>()
		{
			@Override
			public WebElement apply(WebDriver driver)
			{
				return elementIfVisible(context.findElement(locator));
			}

			@Override
			public String toString()
			{
				return "visibilityOfElementLocated " + context + ":" + locator;
			}
		};
	}

	private static WebElement elementIfVisible(WebElement element)
	{
		return element.isDisplayed() ? element : null;
	}

	public static ExpectedCondition<WebElement> presenceOfElement(final WebElement element)
	{
		return new ExpectedCondition<WebElement>()
		{
			@Override
			public WebElement apply(WebDriver driver)
			{
				try
				{
					element.isDisplayed();
					return element;
				}
				catch( StaleElementReferenceException ser )
				{
					return null;
				}
				catch( NoSuchElementException e )
				{
					return null;
				}
			}

			@Override
			public String toString()
			{
				return "presenceOfElement " + element;
			}
		};
	}

	public static ExpectedCondition<WebElement> elementTextToBe(final WebElement element, final String text)
	{
		return new ExpectedCondition<WebElement>()
		{
			private String lastValue;

			@Override
			public WebElement apply(WebDriver driver)
			{
				lastValue = element.getText();
				if( text.equals(lastValue) )
				{
					return element;
				}
				return null;
			}

			@Override
			public String toString()
			{
				return String.format("elementTextToBe %s '%s' (last: '%s')", element, text, lastValue);
			}
		};
	}

	public static ExpectedCondition<WebElement> elementAttributeToBe(final WebElement element, final String attribute,
		final String value)
	{
		return new ExpectedCondition<WebElement>()
		{
			private String lastValue;

			@Override
			public WebElement apply(WebDriver driver)
			{
				lastValue = element.getAttribute(attribute);
				if( value.equals(lastValue) )
				{
					return element;
				}
				return null;
			}

			@Override
			public String toString()
			{
				return String.format("attribute '%s' = '%s' (last: '%s') in element %s", attribute, value, lastValue,
					element);
			}
		};
	}

	public static ExpectedCondition<WebElement> elementAttributeToContain(final WebElement element,
		final String attribute, final String value)
	{
		final WebElement realElement;
		if( element instanceof RefreshableElement )
		{
			realElement = ((RefreshableElement) element).findNonWrapped();
		}
		else
		{
			realElement = element;
		}

		return new ExpectedCondition<WebElement>()
		{
			private String lastValue;

			@Override
			public WebElement apply(WebDriver driver)
			{
				lastValue = realElement.getAttribute(attribute);

				if( !Check.isEmpty(lastValue) && lastValue.contains(value) )
				{
					return element;
				}
				return null;
			}

			@Override
			public String toString()
			{
				return String.format("attribute '%s' = '%s' (last: '%s') in element %s", attribute, value, lastValue,
					element);
			}
		};
	}

	public static ExpectedCondition<Boolean> stalenessOrNonPresenceOf(WebElement element)
	{
		final WebElement realElement;
		if( element instanceof RefreshableElement )
		{
			realElement = ((RefreshableElement) element).findNonWrapped();
		}
		else
		{
			realElement = element;
		}
		return new ExpectedCondition<Boolean>()
		{
			@Override
			public Boolean apply(WebDriver driver)
			{
				try
				{
					realElement.isDisplayed();
					return false;
				}
				catch( StaleElementReferenceException ser )
				{
					return true;
				}
				catch( NoSuchElementException e )
				{
					return true;
				}
			}

			@Override
			public String toString()
			{
				return "stalenessOrNonPresenceOf " + realElement;
			}
		};
	}

	// Is this required?
	public static ExpectedCondition<WebElement> invisibilityOf(final WebElement element)
	{
		return new ExpectedCondition<WebElement>()
		{
			@Override
			public WebElement apply(WebDriver driver)
			{
				return element.isDisplayed() ? null : element;
			}

			@Override
			public String toString()
			{
				return "invisibility of " + element;
			}

		};
	}

	/**
	 * An Expectation for checking that an element is either invisible or not
	 * present on the DOM.
	 * 
	 * @param locator used to find the element
	 */
	public static ExpectedCondition<Boolean> invisibilityOfElementLocated(final SearchContext context, final By locator)
	{
		return new ExpectedCondition<Boolean>()
		{
			@Override
			public Boolean apply(WebDriver driver)
			{
				try
				{
					return !context.findElement(locator).isDisplayed();
				}
				catch( NoSuchElementException e )
				{
					return true;
				}
				catch( StaleElementReferenceException e )
				{
					return true;
				}
			}

			@Override
			public String toString()
			{
				return "element to no longer be visible: " + locator;
			}
		};
	}

	public static ExpectedCondition<WebDriver> frameToBeAvailableAndSwitchToIt(final SearchContext context, final By by)
	{
		return new ExpectedCondition<WebDriver>()
		{
			@Override
			public WebDriver apply(WebDriver from)
			{
				try
				{
					from.switchTo().defaultContent();
					return from.switchTo().frame(context.findElement(by));
				}
				catch( NoSuchElementException nsee )
				{
					return null;
				}
				catch( NoSuchFrameException e )
				{
					return null;
				}
			}

			@Override
			public String toString()
			{
				return "frame to be available: " + context + ":" + by;
			}
		};
	}

	public static ExpectedCondition<String> newWindowOpenedAndSwitchedTo(WebDriver driver)
	{
		final Set<String> previousHandles = driver.getWindowHandles();
		return new ExpectedCondition<String>()
		{

			@Override
			public String apply(WebDriver driver)
			{
				Set<String> currentHandles = driver.getWindowHandles();
				if( !currentHandles.equals(previousHandles) )
				{
					for( String handle : currentHandles )
					{
						if( !previousHandles.contains(handle) )
						{
							driver.switchTo().window(handle);
							return handle;
						}
					}
				}
				return null;
			}

		};

	}

	public static ExpectedCondition<WebElement> updateOfElement(final WebElement element)
	{
		final WebElement realElement;
		if( element instanceof RefreshableElement )
		{
			realElement = ((RefreshableElement) element).findNonWrapped();
		}
		else
		{
			throw new Error("Will only work on wrapped elements");
		}
		return new ExpectedCondition<WebElement>()
		{
			private boolean checkingStale = true;

			@Override
			public WebElement apply(WebDriver driver)
			{
				if( checkingStale )
				{
					try
					{
						realElement.isDisplayed();
						return null;
					}
					catch( StaleElementReferenceException se )
					{
						checkingStale = false;
					}
				}
				return elementIfVisible(element);
			}

			@Override
			public String toString()
			{
				return "updateOfElement " + element;
			}
		};
	}

	public static ExpectedCondition<List<WebElement>> numberOfElementLocated(final SearchContext context, final By by,
		final int count)
	{
		return new ExpectedCondition<List<WebElement>>()
		{
			@Override
			public List<WebElement> apply(WebDriver driver)
			{
				List<WebElement> elems = context.findElements(by);
				if( elems.size() == count )
				{
					return elems;
				}
				return null;
			}

			@Override
			public String toString()
			{
				return "numberOfElementLocated " + context + " by:" + by + " count:" + count;
			}
		};
	}

	public static ExpectedCondition<WebElement> updateFromElementTo(final WebElement from, final WebElement to)
	{
		final WebElement realElement;
		if( from instanceof RefreshableElement )
		{
			realElement = ((RefreshableElement) from).findNonWrapped();
		}
		else
		{
			throw new Error("Will only work on wrapped elements");
		}
		return new ExpectedCondition<WebElement>()
		{
			private boolean checkingStale = true;

			@Override
			public WebElement apply(WebDriver driver)
			{
				if( checkingStale )
				{
					try
					{
						realElement.isDisplayed();
						return null;
					}
					catch( StaleElementReferenceException se )
					{
						checkingStale = false;
					}
				}
				return elementIfVisible(to);
			}

			@Override
			public String toString()
			{
				return "updateFromElementTo " + from + " to " + to;
			}
		};
	}

	/**
	 * An expectation for checking if the given text is present in the specified
	 * element.
	 */
	public static ExpectedCondition<Boolean> textToEqualInElement(final SearchContext searchContext, final By locator,
		final String text)
	{
		return new ExpectedCondition<Boolean>()
		{
			private String lastValue;

			@Override
			public Boolean apply(WebDriver from)
			{
				try
				{
					SearchContext realContext = searchContext;
					if( realContext == null )
					{
						realContext = from;
					}
					lastValue = realContext.findElement(locator).getText();
					return lastValue.equals(text);
				}
				catch( StaleElementReferenceException e )
				{
					return null;
				}
			}

			@Override
			public String toString()
			{
				return String.format("text ('%s') (last: '%s') to match in element found by %s:%s", text, lastValue,
					searchContext, locator);
			}
		};
	}

	public static ExpectedCondition<Boolean> textToBePresentInElement(final WebElement element, final String text)
	{
		return new ExpectedCondition<Boolean>()
		{
			private String lastValue;

			@Override
			public Boolean apply(WebDriver from)
			{
				try
				{
					lastValue = element.getText();
					return lastValue.contains(text);
				}
				catch( StaleElementReferenceException e )
				{
					return null;
				}
			}

			@Override
			public String toString()
			{
				return String.format("text ('%s') (last: '%s') to match in element %s ", text, lastValue, element);
			}
		};
	}

	public static ExpectedCondition<Alert> acceptAlert()
	{
		return new ExpectedCondition<Alert>()
		{
			@Override
			public Alert apply(WebDriver input)
			{
				try
				{
					Alert alert = input.switchTo().alert();
					alert.accept();
					return alert;
				}
				catch( NoAlertPresentException e )
				{
					return null;
				}
			}

			@Override
			public String toString()
			{
				return "alert to be present and accept";
			}
		};
	}

	public static ExpectedCondition<WebElement> elementIsFocused(final WebElement element)
	{
		return new ExpectedCondition<WebElement>()
		{
			@Override
			public WebElement apply(WebDriver driver)
			{
				try
				{
					if( element.equals(driver.switchTo().activeElement()) )
					{
						return element;
					}
					else
					{
						return null;
					}
				}
				catch( NoSuchElementException e )
				{
					return null;
				}
			}
		};
	}

	public static ExpectedCondition<Boolean> childCount(final WebElement element, final int expectedCount)
	{
		return new ExpectedCondition<Boolean>()
		{
			@Override
			public Boolean apply(WebDriver driver)
			{
				try
				{
					List<WebElement> children = element.findElements(By.xpath("./*"));
					if( children.size() == expectedCount )
					{
						return true;
					}
					return null;
				}
				catch( NoSuchElementException e )
				{
					return null;
				}
			}
		};
	}

	public static ExpectedCondition<?> ajaxUpdate(WebElement ajaxElem)
	{
		WebElement firstChild = AbstractPage.elementIfPresent(ajaxElem, XPATH_FIRSTELEM);
		if( firstChild != null )
		{
			return ExpectedConditions2.updateOfElement(firstChild);
		}
		return ExpectedConditions2.visibilityOfElementLocated(ajaxElem, XPATH_FIRSTELEM);
	}

	public static ExpectedCondition<?> ajaxUpdateExpect(WebElement ajaxElem, WebElement expectedElement)
	{
		WebElement firstChild = AbstractPage.elementIfPresent(ajaxElem, XPATH_FIRSTELEM);
		if( firstChild != null )
		{
			return ExpectedConditions2.updateFromElementTo(firstChild, expectedElement);
		}
		return ExpectedConditions.visibilityOf(expectedElement);
	}

	public static ExpectedCondition<?> ajaxUpdateEmpty(WebElement ajaxElem)
	{
		return ExpectedConditions2.invisibilityOfElementLocated(ajaxElem, XPATH_FIRSTELEM);
	}
}
