package com.sottti.android.app.template.app

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.sottti.android.app.template.presentation.design.system.top.bars.ui.MAIN_TOP_BAR_BACK_NAVIGATION_TEST_TAG
import com.sottti.android.app.template.presentation.utils.test.OrientationTest
import dagger.hilt.android.testing.HiltAndroidTest
import org.junit.Test

@HiltAndroidTest
internal class AppFlowsTest(
    orientation: OrientationTest,
) : IntegrationTest(orientation) {

    @Test
    fun caseSearch_submitQuery_articleDetails_navigateBack() {
        with(rule) {
            onNodeWithText(SCREEN_TITLE).assertIsDisplayed()

            onNode(hasSetTextAction()).performTextInput(LATROCINIO_REPORT)

            waitForIdle()

            onNodeWithText(SUBMIT_LABEL).performClick()

            waitUntil() {
                onAllNodesWithText(LATROCINIO_ARTICLE)
                    .fetchSemanticsNodes()
                    .isNotEmpty()
            }

            onAllNodesWithText(LATROCINIO_ARTICLE)[FIRST_RESULT].performClick()

            waitForIdle()

            onNodeWithTag(MAIN_TOP_BAR_BACK_NAVIGATION_TEST_TAG).assertIsDisplayed()
            onNodeWithTag(MAIN_TOP_BAR_BACK_NAVIGATION_TEST_TAG).performClick()

            waitForIdle()

            onNodeWithText(SUBMIT_LABEL).assertIsDisplayed()
        }
    }

    private companion object {
        private const val FIRST_RESULT = 0
        private const val SCREEN_TITLE = "Assistente criminal"
        private const val SUBMIT_LABEL = "Analisar caso"
        private const val LATROCINIO_ARTICLE = "Art. 157, § 3º"
        private const val LATROCINIO_REPORT =
            "O autor praticou latrocínio, roubo seguido de morte da vítima."
    }
}
