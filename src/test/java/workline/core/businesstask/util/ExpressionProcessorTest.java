package workline.core.businesstask.util;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import vrds.model.EAttributeType;
import vrds.model.RepoItem;
import vrds.model.RepoItemAttribute;
import workline.core.api.internal.IBusinessTaskHandler;
import workline.core.repo.listener.RepoItemValueReference;

public class ExpressionProcessorTest {
    private ExpressionProcessor testSubject;

    @Mock
    private IBusinessTaskHandler businessTaskHandler;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);

        testSubject = new ExpressionProcessor();

        setTestSubjectProperties();
    }

    @Test
    public void testSimpleStringVariable() throws Exception {
        String variableName = "variableName";
        String expression = variableName;

        String variableValue = "value";

        ExpectedRepoItemValueReferenceData expected = new ExpectedRepoItemValueReferenceData(null, null);

        test(variableName, expression, variableValue, expected);
    }

    @Test
    public void testSimpleRepoItemVariable() throws Exception {
        String variableName = "variableName";
        String expression = variableName;
        RepoItem variableValue = new RepoItem();
        addValue(variableValue, variableName, EAttributeType.STRING, "value");

        ExpectedRepoItemValueReferenceData expected = new ExpectedRepoItemValueReferenceData(variableValue, null);

        test(variableName, expression, variableValue, expected);
    }

    @Test
    public void testSimpleStringVariableExpression() throws Exception {
        String variableName = "variableName";
        String expression = variableName + ".attributeName";
        RepoItem variableValue = new RepoItem();
        addValue(variableValue, "attributeName", EAttributeType.STRING, "value");

        ExpectedRepoItemValueReferenceData expected = new ExpectedRepoItemValueReferenceData(variableValue, "attributeName");

        test(variableName, expression, variableValue, expected);
    }

    @Test
    public void testSimpleRepoItemVariableExpression() throws Exception {
        String variableName = "variableName";
        String expression = variableName + ".attributeName";
        RepoItem variableValue = new RepoItem();
        RepoItem repoItem2 = new RepoItem();
        addValue(variableValue, "attributeName", EAttributeType.REPO_ITEM, repoItem2);

        ExpectedRepoItemValueReferenceData expected = new ExpectedRepoItemValueReferenceData(variableValue, "attributeName");

        test(variableName, expression, variableValue, expected);
    }

    @Test
    public void testStringVariableExpression() throws Exception {
        String variableName = "variableName";
        String expression = variableName + ".attributeName.attributeName2";
        RepoItem variableValue = new RepoItem();
        RepoItem repoItem2 = new RepoItem();
        addValue(variableValue, "attributeName", EAttributeType.REPO_ITEM, repoItem2);
        addValue(repoItem2, "attributeName2", EAttributeType.STRING, "value");

        ExpectedRepoItemValueReferenceData expected = new ExpectedRepoItemValueReferenceData(repoItem2, "attributeName2");

        test(variableName, expression, variableValue, expected);
    }

    @Test
    public void testRepoItemVariableExpression() throws Exception {
        String variableName = "variableName";
        String expression = variableName + ".attributeName.attributeName2";
        RepoItem variableValue = new RepoItem();
        RepoItem repoItem2 = new RepoItem();
        RepoItem repoItem3 = new RepoItem();
        addValue(variableValue, "attributeName", EAttributeType.REPO_ITEM, repoItem2);
        addValue(repoItem2, "attributeName2", EAttributeType.REPO_ITEM, repoItem3);

        ExpectedRepoItemValueReferenceData expected = new ExpectedRepoItemValueReferenceData(repoItem2, "attributeName2");

        test(variableName, expression, variableValue, expected);
    }

    private void addValue(RepoItem repoItem, String attributeName, EAttributeType type, Object value) {
        RepoItemAttribute attribute = new RepoItemAttribute();
        attribute.setNameAndType(attributeName, type);
        attribute.setValue(value);

        repoItem.getRepoItemAttributes().add(attribute);

    }

    private void test(String variableName, String expression, Object variableValue, ExpectedRepoItemValueReferenceData expected) {
        // GIVEN
        Long processElementId = 1L;
        Mockito.when(businessTaskHandler.readVariable(processElementId, variableName)).thenReturn(variableValue);

        // WHEN
        RepoItemValueReference actual = testSubject.getRepoItemValueReference(processElementId, expression);

        // THEN
        assertResult(expected, actual);
    }

    private void assertResult(ExpectedRepoItemValueReferenceData expected, RepoItemValueReference actual) {
        RepoItem expectedRepoItem = expected.getRepoItem();
        String expectedAttributeName = expected.getAttributeName();

        RepoItem actualRepoItem = actual.getRepoItem();
        String actualAttributeName = actual.getAttributeName();

        Assert.assertEquals(expectedRepoItem, actualRepoItem);
        Assert.assertEquals(expectedAttributeName, actualAttributeName);
    }

    private void setTestSubjectProperties() {
        testSubject.setBusinessTaskHandler(businessTaskHandler);

    }

    private class ExpectedRepoItemValueReferenceData {
        private RepoItem repoItem;
        private String attributeName;

        public ExpectedRepoItemValueReferenceData(RepoItem repoItem, String attributeName) {
            this.repoItem = repoItem;
            this.attributeName = attributeName;
        }

        public RepoItem getRepoItem() {
            return repoItem;
        }

        public String getAttributeName() {
            return attributeName;
        }
    }
}
