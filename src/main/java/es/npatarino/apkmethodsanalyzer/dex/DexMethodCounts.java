/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package es.npatarino.apkmethodsanalyzer.dex;

import es.npatarino.apkmethodsanalyzer.config.Config;

import java.util.*;

public class DexMethodCounts extends DexCount {

    private Config config;

    public DexMethodCounts(Config config) {
        super(OutputStyle.TREE);
        this.config = config;
    }

    public DexMethodCounts(OutputStyle outputStyle) {
        super(outputStyle);
    }

    @Override
    public void generate(DexData dexData, boolean includeClasses, String packageFilter, int maxDepth, Filter filter) {
        MethodRef[] methodRefs = getMethodRefs(dexData, filter);

        for (MethodRef methodRef : methodRefs) {
            String classDescriptor = methodRef.getDeclClassName();
            String packageName = includeClasses ?
                    Output.descriptorToDot(classDescriptor).replace('$', '.') :
                    Output.packageNameOnly(classDescriptor);
            if (packageFilter != null &&
                    !packageName.startsWith(packageFilter)) {
                continue;
            }
            overallCount++;
            if (outputStyle == OutputStyle.TREE) {
                String packageNamePieces[] = packageName.split("\\.");
                Node packageNode = packageTree;
                int realMaxDepth = maxDepth;
                if (packageName.startsWith("android")
                        || packageName.startsWith("butterknife")
                        || packageName.startsWith("ad")
                        || packageName.startsWith("dalvik")
                        || packageName.startsWith("javax")
                        || packageName.startsWith("junit")
                        || packageName.startsWith("retrofit")
                        || packageName.startsWith("rx")
                        || packageName.startsWith("java")) {
                    realMaxDepth = 1;
                } else if (packageName.startsWith("com.appsflyer")
                        || packageName.startsWith("com.idealista")
                        || packageName.startsWith("com.atinternet")
                        || packageName.startsWith("com.crashlytics")
                        || packageName.startsWith("com.facebook")
                        || packageName.startsWith("com.nineoldandroids")
                        || packageName.startsWith("io.fabric")
                        || packageName.startsWith("org.jfree")
                        || packageName.startsWith("org.junit")
                        || packageName.startsWith("org.simpleframework")
                        || packageName.startsWith("org.springframework")
                        || packageName.startsWith("org.hamcrest")
                        || packageName.startsWith("org.w3c")
                        || packageName.startsWith("org.xml")
                        || packageName.startsWith("org.xmlpull")) {
                    realMaxDepth = 2;
                } else if (packageName.startsWith("com.android.volley")
                        || packageName.startsWith("com.fasterxml.jackson")
                        || packageName.startsWith("com.gc.materialdesign")
                        || packageName.startsWith("com.google")
                        || packageName.startsWith("com.squareup")
                        || packageName.startsWith("com.tundem")
                        || packageName.startsWith("com.apache")
                        || packageName.startsWith("com.nirhart")
                        || packageName.startsWith("org.codehaus")
                        || packageName.startsWith("org.apache")
                        || packageName.startsWith("com.android.volley")
                        || packageName.startsWith("com.android.volley")
                        ) {
                    realMaxDepth = 3;
                }
                for (int i = 0; i < packageNamePieces.length && i < realMaxDepth; i++) {
                    packageNode.count++;
                    String name = packageNamePieces[i];
                    if (packageNode.children.containsKey(name)) {
                        packageNode = packageNode.children.get(name);
                    } else {
                        Node childPackageNode = new Node();
                        if (name.length() == 0) {
                            // This method is declared in a class that is part of the default package.
                            // Typical examples are methods that operate on arrays of primitive data types.
                            name = "<default>";
                        }
                        packageNode.children.put(name, childPackageNode);
                        packageNode = childPackageNode;
                    }
                }
                packageNode.count++;
            } else if (outputStyle == OutputStyle.FLAT) {
                IntHolder count = packageCount.get(packageName);
                if (count == null) {
                    count = new IntHolder();
                    packageCount.put(packageName, count);
                }
                count.value++;
            }
        }
    }

    private static MethodRef[] getMethodRefs(DexData dexData, Filter filter) {
        MethodRef[] methodRefs = dexData.getMethodRefs();
        out.println("Read in " + methodRefs.length + " method IDs.");
        if (filter == Filter.ALL) {
            return methodRefs;
        }

        ClassRef[] externalClassRefs = dexData.getExternalReferences();
        out.println("Read in " + externalClassRefs.length +
                " external class references.");
        Set<MethodRef> externalMethodRefs = new HashSet<MethodRef>();
        for (ClassRef classRef : externalClassRefs) {
            Collections.addAll(externalMethodRefs, classRef.getMethodArray());
        }
        out.println("Read in " + externalMethodRefs.size() +
                " external method references.");
        List<MethodRef> filteredMethodRefs = new ArrayList<MethodRef>();
        for (MethodRef methodRef : methodRefs) {
            boolean isExternal = externalMethodRefs.contains(methodRef);
            if ((filter == Filter.DEFINED_ONLY && !isExternal) ||
                    (filter == Filter.REFERENCED_ONLY && isExternal)) {
                filteredMethodRefs.add(methodRef);
            }
        }
        out.println("Filtered to " + filteredMethodRefs.size() + " " +
                (filter == Filter.DEFINED_ONLY ? "defined" : "referenced") +
                " method IDs.");
        return filteredMethodRefs.toArray(
                new MethodRef[filteredMethodRefs.size()]);
    }
}
