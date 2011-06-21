/*
 *   Project: Speedith.Core
 * 
 * File name: SplitSpiders.java
 *    Author: Matej Urbas [matej.urbas@gmail.com]
 * 
 *  Copyright © 2011 Matej Urbas
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package speedith.core.reasoning.rules;

import static speedith.core.i18n.Translations.*;
import java.util.List;
import speedith.core.lang.SpiderDiagram;
import speedith.core.reasoning.BasicInferenceRule;
import speedith.core.reasoning.InferenceRule;
import speedith.core.reasoning.args.RuleArg;
import speedith.core.reasoning.RuleApplicationException;
import speedith.core.reasoning.args.PrimarySDIndexArg;

/**
 *
 * @author Matej Urbas [matej.urbas@gmail.com]
 */
public class SplitSpiders implements InferenceRule, BasicInferenceRule {

    public SpiderDiagram[] apply(RuleArg args, List<SpiderDiagram> goals) throws RuleApplicationException {
        if (goals == null || goals.isEmpty()) {
            throw new RuleApplicationException(i18n("RULE_NO_SUBGOALS"));
        }
        if (args instanceof PrimarySDIndexArg) {
            int sugoalIndex = ((PrimarySDIndexArg)args).getSubgoalIndex();
            int primarySDIndex = ((PrimarySDIndexArg)args).getPrimarySDIndex();
            // TODO: Finish the rule.
            return goals.toArray(new SpiderDiagram[goals.size()]);
        } else {
            throw new RuleApplicationException(i18n("RULE_INVALID_ARGS"));
        }
    }
}
